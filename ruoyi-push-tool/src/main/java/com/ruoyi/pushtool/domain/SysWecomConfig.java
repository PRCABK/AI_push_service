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
}