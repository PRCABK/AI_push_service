package com.ruoyi.pushtool.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 推送任务记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysPushTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 对外请求ID，方便排查 */
    private String requestId;

    /** 使用的AI配置 */
    private Long aiConfigId;

    /** 企业微信配置 */
    private Long wecomConfigId;

    /** 使用的提示词Key */
    private String promptKey;

    /** 消息类型 */
    private String msgType;

    /** 实际接收人 */
    private String receivers;

    /** 选取的接收人分组 */
    private String receiverGroup;

    /** 推送数据快照(JSON) */
    private String payload;

    /** AI输出快照 */
    private String aiResult;

    /** 状态 SUCCESS/FAIL */
    private String status;

    /** 错误信息 */
    private String errorMsg;

    /** 发送时间 */
    private Date sendTime;
}
