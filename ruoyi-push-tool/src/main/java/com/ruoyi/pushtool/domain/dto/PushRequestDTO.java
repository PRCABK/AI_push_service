package com.ruoyi.pushtool.domain.dto;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * 推送请求对象
 */
@Data
public class PushRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 请求ID，便于幂等与排查 */
    private String requestId;

    /** AI配置ID */
    private Long aiConfigId;

    /** 微信配置ID */
    private Long wecomConfigId;

    /** 提示词模版Key */
    private String promptKey;

    /** 是否启用AI分析(兼容enableAi) */
    private Boolean useAi;

    /** 是否启用AI分析 */
    private Boolean enableAi;

    /** 消息类型: text 或 textcard */
    private String msgType;

    /** 指定接收人分组名称 */
    private String receiverGroup;

    /** 指定接收人(UserID)，不传则使用配置里的默认人 */
    private String toUser;

    /** 业务数据负载 (包含 title, content, url 等) */
    private Map<String, Object> data;
}
