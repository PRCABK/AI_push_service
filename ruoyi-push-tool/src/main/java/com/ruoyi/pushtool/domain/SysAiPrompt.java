package com.ruoyi.pushtool.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提示词模版对象 sys_ai_prompt
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysAiPrompt extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 模版名称 */
    private String promptName;

    /** 调用Key */
    private String promptKey;

    /** Prompt内容 */
    private String content;
}