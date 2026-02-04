package com.ruoyi.pushtool.mapper;

import com.ruoyi.pushtool.domain.SysAiPrompt;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysAiPromptMapper {
    /** 根据PromptKey查询提示词 */
    SysAiPrompt selectByPromptKey(String promptKey);
}