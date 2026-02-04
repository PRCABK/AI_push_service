package com.ruoyi.pushtool.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI平台配置对象 sys_ai_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAiConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 配置名称 */
    private String configName;

    /** API Key */
    private String apiKey;

    /** 接口地址 */
    private String apiUrl;

    /** 模型名称 */
    private String modelName;

    /** 温度参数 */
    private Double temperature;
}