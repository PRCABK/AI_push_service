package com.ruoyi.pushtool.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.pushtool.domain.dto.PushRequestDTO;
import com.ruoyi.pushtool.domain.SysAiConfig;
import com.ruoyi.pushtool.domain.SysWecomConfig;
import com.ruoyi.pushtool.domain.SysAiPrompt;
import com.ruoyi.pushtool.mapper.SysAiConfigMapper;
import com.ruoyi.pushtool.mapper.SysAiPromptMapper;
import com.ruoyi.pushtool.mapper.SysWecomConfigMapper;
import com.ruoyi.pushtool.service.IPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class PushServiceImpl implements IPushService {

    @Autowired
    private SysAiConfigMapper aiConfigMapper;
    @Autowired
    private SysWecomConfigMapper wecomConfigMapper;
    @Autowired
    private SysAiPromptMapper promptMapper;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public void executePush(PushRequestDTO request) {
        // 1. 获取微信配置
        SysWecomConfig wecom = wecomConfigMapper.selectSysWecomConfigById(request.getWecomConfigId());
        if (wecom == null) throw new ServiceException("微信配置不存在");

        String content = request.getData().getOrDefault("content", "").toString();
        String title = request.getData().getOrDefault("title", "系统提醒").toString();

        // 2. AI 分析逻辑
        if (Boolean.TRUE.equals(request.getUseAi())) {
            SysAiConfig ai = aiConfigMapper.selectSysAiConfigById(request.getAiConfigId());
            SysAiPrompt prompt = promptMapper.selectByPromptKey(request.getPromptKey());

            if (ai != null && prompt != null) {
                log.info("开始 AI 分析，模型: {}", ai.getModelName());
                String aiResult = callAi(content, ai, prompt);
                content = content + "\n\n🤖 AI 分析结果：\n" + aiResult;
            }
        }

        // 3. 执行推送
        String toUser = StringUtils.isNotEmpty(request.getToUser()) ? request.getToUser() : wecom.getDefaultUser();
        String token = getAccessToken(wecom);

        if (StringUtils.isEmpty(token)) throw new ServiceException("获取微信Token失败");

        if ("textcard".equals(request.getMsgType())) {
            sendTextCard(title, content, wecom, toUser, token, request.getData().getOrDefault("url", "").toString());
        } else {
            sendTextMessage(content, wecom, toUser, token);
        }
    }

    /**
     * AI 接口调用逻辑
     */
    private String callAi(String data, SysAiConfig config, SysAiPrompt prompt) {
        try {
            URL url = new URL(config.getApiUrl() + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", config.getModelName());
            bodyMap.put("messages", Collections.singletonList(new HashMap<String, String>() {{
                put("role", "user");
                put("content", prompt.getContent() + "\n数据内容：\n" + data);
            }}));
            bodyMap.put("temperature", config.getTemperature());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(gson.toJson(bodyMap).getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    JsonObject response = gson.fromJson(br, JsonObject.class);
                    return response.getAsJsonArray("choices").get(0).getAsJsonObject().get("message").getAsJsonObject().get("content").getAsString();
                }
            }
        } catch (Exception e) {
            log.error("AI分析异常", e);
        }
        return "AI分析请求失败";
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
                return json.has("access_token") ? json.get("access_token").getAsString() : null;
            }
        } catch (Exception e) {
            log.error("获取Token异常", e);
        }
        return null;
    }

    /**
     * 发送文本消息
     */
    private void sendTextMessage(String content, SysWecomConfig wecom, String toUser, String token) {
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        JsonObject root = new JsonObject();
        root.addProperty("touser", toUser);
        root.addProperty("msgtype", "text");
        root.addProperty("agentid", wecom.getAgentId());
        JsonObject text = new JsonObject();
        text.addProperty("content", content);
        root.add("text", text);

        postToWeCom(sendUrl, root);
    }

    /**
     * 发送卡片消息
     */
    private void sendTextCard(String title, String content, SysWecomConfig wecom, String toUser, String token, String url) {
        String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;

        StringBuilder desc = new StringBuilder();
        desc.append("<div class=\"gray\">").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append("</div>");
        desc.append("<div class=\"normal\">").append(content.replace("\n", "<br>")).append("</div>");

        JsonObject textcard = new JsonObject();
        textcard.addProperty("title", title);
        textcard.addProperty("description", desc.toString());
        textcard.addProperty("url", StringUtils.isNotEmpty(url) ? url : "URL_PLACEHOLDER");
        textcard.addProperty("btntxt", "更多详情");

        JsonObject root = new JsonObject();
        root.addProperty("touser", toUser);
        root.addProperty("msgtype", "textcard");
        root.addProperty("agentid", wecom.getAgentId());
        root.add("textcard", textcard);

        postToWeCom(sendUrl, root);
    }

    private void postToWeCom(String urlStr, JsonObject body) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(gson.toJson(body).getBytes(StandardCharsets.UTF_8));
            }
            // 简单读取响应确保发送完成
            conn.getInputStream().close();
            log.info("微信推送指令已提交");
        } catch (Exception e) {
            log.error("微信推送异常", e);
        }
    }
}