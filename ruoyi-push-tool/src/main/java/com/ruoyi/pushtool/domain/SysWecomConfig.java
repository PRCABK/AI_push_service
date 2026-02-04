package com.ruoyi.pushtool.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推送平台配置对象 sys_wecom_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysWecomConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 配置名称 */
    private String configName;

    /** 企业ID */
    private String corpId;

    /** 应用Secret */
    private String secret;

    /** 应用AgentId */
    private Integer agentId;

    /** 默认接收人ID */
    private String defaultUser;

    /** 默认接收部门 */
    private String defaultParty;

    /** 接收人分组(JSON 字符串) */
    private String receiverGroups;

    /** 默认消息类型 */
    private String defaultMsgType;

    /** 文本模板 */
    private String textTemplate;

    /** 卡片标题模板 */
    private String cardTitleTemplate;

    /** 卡片内容模板 */
    private String cardContentTemplate;

    /** 卡片跳转链接模板 */
    private String cardUrlTemplate;

    /** 卡片按钮文案 */
    private String cardBtnText;
}
