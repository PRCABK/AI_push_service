package com.ruoyi.pushtool.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pushtool.domain.SysAiConfig;
import com.ruoyi.pushtool.domain.SysAiPrompt;
import com.ruoyi.pushtool.domain.SysPushTask;
import com.ruoyi.pushtool.domain.SysWecomConfig;
import com.ruoyi.pushtool.domain.dto.PushRequestDTO;
import com.ruoyi.pushtool.mapper.SysAiConfigMapper;
import com.ruoyi.pushtool.mapper.SysAiPromptMapper;
import com.ruoyi.pushtool.mapper.SysPushTaskMapper;
import com.ruoyi.pushtool.mapper.SysWecomConfigMapper;
import com.ruoyi.pushtool.service.IPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class PushServiceImpl implements IPushService {

    private static final DateTimeFormatter CARD_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private SysAiConfigMapper aiConfigMapper;
    @Autowired
    private SysWecomConfigMapper wecomConfigMapper;
    @Autowired
    private SysAiPromptMapper promptMapper;
    @Autowired
    private SysPushTaskMapper pushTaskMapper;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public void executePush(PushRequestDTO request) {
        if (request.getWecomConfigId() == null) {
            throw new ServiceException("wecomConfigId 不能为空");
        }
        SysWecomConfig wecom = wecomConfigMapper.selectSysWecomConfigById(request.getWecomConfigId());
        if (wecom == null) {
            throw new ServiceException("微信配置不存在");
        }

        Map<String, Object> payload = new HashMap<>();
        if (request.getData() != null) {
            payload.putAll(request.getData());
        }

        String requestId = StringUtils.isNotEmpty(request.getRequestId()) ? request.getRequestId() : UUID.randomUUID().toString();
        SysPushTask task = buildTaskSkeleton(requestId, request, payload);
        task.setWecomConfigId(wecom.getId());

        ServiceException pendingEx = null;
        try {
            String baseContent = Objects.toString(payload.getOrDefault("content", ""), "");
            String baseTitle = payload.containsKey("title") ? Objects.toString(payload.get("title"), "") : "系统提醒";
            String baseUrl = Objects.toString(payload.getOrDefault("url", ""), "");

            String aiResult = null;
            SysAiConfig aiConfig = null;
            if (isAiEnabled(request)) {
                aiConfig = aiConfigMapper.selectSysAiConfigById(request.getAiConfigId());
                SysAiPrompt prompt = promptMapper.selectByPromptKey(request.getPromptKey());
                if (aiConfig != null && prompt != null) {
                    aiResult = callAi(payload, aiConfig, prompt);
                } else if (aiConfig == null) {
                    log.warn("启用AI但未找到配置, id={}", request.getAiConfigId());
                } else {
                    log.warn("启用AI但未找到提示词, key={}", request.getPromptKey());
                }
            }
            if (aiConfig != null) {
                task.setAiConfigId(aiConfig.getId());
            }
            if (StringUtils.isNotEmpty(aiResult)) {
                payload.put("aiResult", aiResult);
                payload.put("ai_content", aiResult);
            }
            task.setAiResult(aiResult);
            payload.putIfAbsent("title", baseTitle);
            payload.putIfAbsent("content", baseContent);

            String msgType = StringUtils.defaultIfBlank(request.getMsgType(),
                    StringUtils.defaultIfBlank(wecom.getDefaultMsgType(), "text"));
            task.setMsgType(msgType);

            String receivers = resolveReceivers(request, wecom);
            if (StringUtils.isEmpty(receivers)) {
                throw new ServiceException("未找到可用的接收人配置");
            }
            task.setReceivers(receivers);

            String defaultContent = assembleDefaultContent(baseContent, aiResult);
            String textBody = renderTemplate(wecom.getTextTemplate(), payload, defaultContent);
            String cardTitle = renderTemplate(wecom.getCardTitleTemplate(), payload, baseTitle);
            String cardDesc = renderTemplate(wecom.getCardContentTemplate(), payload, defaultContent);
            String cardUrl = renderTemplate(wecom.getCardUrlTemplate(), payload, baseUrl);
            String btnText = StringUtils.defaultIfBlank(renderTemplate(wecom.getCardBtnText(), payload, "更多详情"), "更多详情");

            String token = getAccessToken(wecom);
            if (StringUtils.isEmpty(token)) {
                throw new ServiceException("获取微信Token失败");
            }

            if ("textcard".equalsIgnoreCase(msgType)) {
                sendTextCard(cardTitle, cardDesc, wecom, receivers, token, cardUrl, btnText);
            } else {
                sendTextMessage(textBody, wecom, receivers, token);
            }
            task.setStatus("SUCCESS");
        } catch (ServiceException e) {
            pendingEx = e;
            task.setStatus("FAIL");
            task.setErrorMsg(limitError(e.getMessage()));
        } catch (RuntimeException e) {
            pendingEx = new ServiceException("推送失败: " + e.getMessage(), e);
            task.setStatus("FAIL");
            task.setErrorMsg(limitError(e.getMessage()));
        } finally {
            task.setSendTime(new Date());
            task.setCreateTime(new Date());
            task.setPayload(gson.toJson(payload));
            try {
                pushTaskMapper.insertSysPushTask(task);
            } catch (Exception recordEx) {
                log.error("记录推送任务失败", recordEx);
            }
        }
        if (pendingEx != null) {
            throw pendingEx;
        }
    }

    private SysPushTask buildTaskSkeleton(String requestId, PushRequestDTO request, Map<String, Object> payload) {
        SysPushTask task = new SysPushTask();
        task.setRequestId(requestId);
        task.setAiConfigId(request.getAiConfigId());
        task.setWecomConfigId(request.getWecomConfigId());
        task.setPromptKey(request.getPromptKey());
        task.setReceiverGroup(request.getReceiverGroup());
        task.setStatus("INIT");
        return task;
    }

    private boolean isAiEnabled(PushRequestDTO request) {
        return Boolean.TRUE.equals(request.getUseAi()) || Boolean.TRUE.equals(request.getEnableAi());
    }

    /**
     * AI 接口调用逻辑
     */
    private String callAi(Map<String, Object> payload, SysAiConfig config, SysAiPrompt prompt) {
        try {
            URL url = new URL(config.getApiUrl() + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", config.getModelName());
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(new HashMap<String, String>() {{
                put("role", "system");
                put("content", prompt.getContent());
            }});
            messages.add(new HashMap<String, String>() {{
                put("role", "user");
                put("content", "请基于以下数据进行分析：\n" + gson.toJson(payload));
            }});
            bodyMap.put("messages", messages);
            if (config.getTemperature() != null) {
                bodyMap.put("temperature", config.getTemperature());
            }
            if (config.getMaxTokens() != null) {
                bodyMap.put("max_tokens", config.getMaxTokens());
            }
            if (config.getTopP() != null) {
                bodyMap.put("top_p", config.getTopP());
            }
            if (config.getPresencePenalty() != null) {
                bodyMap.put("presence_penalty", config.getPresencePenalty());
            }
            if (config.getFrequencyPenalty() != null) {
                bodyMap.put("frequency_penalty", config.getFrequencyPenalty());
            }

            try (OutputStream os = conn.getOutputStream()) {
                os.write(gson.toJson(bodyMap).getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    JsonObject response = gson.fromJson(br, JsonObject.class);
                    return response.getAsJsonArray("choices").get(0).getAsJsonObject()
                            .get("message").getAsJsonObject().get("content").getAsString();
                }
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    log.error("AI接口调用失败: {}", br.readLine());
                }
            }
        } catch (Exception e) {
            log.error("AI分析异常", e);
        }
        return null;
    }

    /**
     * 获取企业微信 AccessToken
     */
    private String getAccessToken(SysWecomConfig wecom) {
        String urlStr = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + wecom.getCorpId() + "&corpsecret=" + wecom.getSecret();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject json = gson.fromJson(isr, JsonObject.class);
                if (json.has("errcode") && json.get("errcode").getAsInt() != 0) {
                    throw new ServiceException("获取Token失败: " + json.get("errmsg").getAsString());
                }
                return json.has("access_token") ? json.get("access_token").getAsString() : null;
            }
        } catch (IOException e) {
            throw new ServiceException("获取Token异常: " + e.getMessage());
        }
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage(String content, SysWecomConfig wecom, String toUser, String token) {
        JsonObject root = new JsonObject();
        root.addProperty("touser", toUser);
        root.addProperty("msgtype", "text");
        root.addProperty("agentid", wecom.getAgentId());
        if (StringUtils.isNotEmpty(wecom.getDefaultParty())) {
            root.addProperty("toparty", wecom.getDefaultParty());
        }
        JsonObject text = new JsonObject();
        text.addProperty("content", content);
        root.add("text", text);
        postToWeCom(token, root);
    }

    /**
     * 发送卡片消息
     */
    private void sendTextCard(String title, String content, SysWecomConfig wecom, String toUser, String token, String url, String btnText) {
        JsonObject textcard = new JsonObject();
        StringBuilder desc = new StringBuilder();
        desc.append("<div class=\"gray\">").append(LocalDateTime.now().format(CARD_TIME_FORMATTER)).append("</div>");
        desc.append("<div class=\"normal\">").append(content.replace("\n", "<br>")).append("</div>");

        textcard.addProperty("title", title);
        textcard.addProperty("description", desc.toString());
        textcard.addProperty("url", StringUtils.isNotEmpty(url) ? url : "https://work.weixin.qq.com/");
        textcard.addProperty("btntxt", StringUtils.defaultIfBlank(btnText, "更多详情"));

        JsonObject root = new JsonObject();
        root.addProperty("touser", toUser);
        root.addProperty("msgtype", "textcard");
        root.addProperty("agentid", wecom.getAgentId());
        if (StringUtils.isNotEmpty(wecom.getDefaultParty())) {
            root.addProperty("toparty", wecom.getDefaultParty());
        }
        root.add("textcard", textcard);

        postToWeCom(token, root);
    }

    private void postToWeCom(String token, JsonObject body) {
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        try {
            URL url = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(gson.toJson(body).getBytes(StandardCharsets.UTF_8));
            }
            int status = conn.getResponseCode();
            java.io.InputStream stream = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
            if (stream == null) {
                throw new ServiceException("企业微信返回空响应");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                JsonObject resp = gson.fromJson(br, JsonObject.class);
                if (status >= 400 || resp.get("errcode").getAsInt() != 0) {
                    throw new ServiceException("企业微信发送失败: " + resp.get("errmsg").getAsString());
                }
                log.info("微信推送完成: {}", resp);
            }
        } catch (IOException e) {
            throw new ServiceException("企业微信推送异常: " + e.getMessage());
        }
    }

    private String resolveReceivers(PushRequestDTO request, SysWecomConfig config) {
        if (StringUtils.isNotEmpty(request.getToUser())) {
            return request.getToUser();
        }
        if (StringUtils.isNotEmpty(request.getReceiverGroup())) {
            Map<String, String> groupMap = parseReceiverGroups(config.getReceiverGroups());
            String groupUsers = groupMap.get(request.getReceiverGroup());
            if (StringUtils.isNotEmpty(groupUsers)) {
                return groupUsers;
            }
        }
        return config.getDefaultUser();
    }

    private Map<String, String> parseReceiverGroups(String rawJson) {
        Map<String, String> map = new LinkedHashMap<>();
        if (StringUtils.isEmpty(rawJson)) {
            return map;
        }
        try {
            JsonArray array = gson.fromJson(rawJson, JsonArray.class);
            for (int i = 0; i < array.size(); i++) {
                JsonObject obj = array.get(i).getAsJsonObject();
                if (obj.has("name") && obj.has("users")) {
                    map.put(obj.get("name").getAsString(), obj.get("users").getAsString());
                }
            }
        } catch (Exception e) {
            log.warn("接收人分组解析失败: {}", rawJson, e);
        }
        return map;
    }

    private String renderTemplate(String template, Map<String, Object> payload, String fallback) {
        if (StringUtils.isBlank(template)) {
            return fallback;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (entry.getValue() != null) {
                result = result.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
        }
        result = result.replace("{{defaultContent}}", fallback);
        return result;
    }

    private String assembleDefaultContent(String baseContent, String aiResult) {
        if (StringUtils.isNotEmpty(aiResult)) {
            return baseContent + "\n\n🤖 AI 分析结果：\n" + aiResult;
        }
        return baseContent;
    }

    private String limitError(String msg) {
        if (msg == null) {
            return null;
        }
        return msg.length() > 500 ? msg.substring(0, 500) : msg;
    }
}
