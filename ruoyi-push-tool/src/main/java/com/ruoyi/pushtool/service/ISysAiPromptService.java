package com.ruoyi.pushtool.service;

import java.util.List;
import com.ruoyi.pushtool.domain.SysAiPrompt;

/**
 * 提示词模版Service接口
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
public interface ISysAiPromptService 
{
    /**
     * 查询提示词模版
     * 
     * @param id 提示词模版主键
     * @return 提示词模版
     */
    public SysAiPrompt selectSysAiPromptById(Long id);

    /**
     * 查询提示词模版列表
     * 
     * @param sysAiPrompt 提示词模版
     * @return 提示词模版集合
     */
    public List<SysAiPrompt> selectSysAiPromptList(SysAiPrompt sysAiPrompt);

    /**
     * 新增提示词模版
     * 
     * @param sysAiPrompt 提示词模版
     * @return 结果
     */
    public int insertSysAiPrompt(SysAiPrompt sysAiPrompt);

    /**
     * 修改提示词模版
     * 
     * @param sysAiPrompt 提示词模版
     * @return 结果
     */
    public int updateSysAiPrompt(SysAiPrompt sysAiPrompt);

    /**
     * 批量删除提示词模版
     * 
     * @param ids 需要删除的提示词模版主键集合
     * @return 结果
     */
    public int deleteSysAiPromptByIds(String ids);

    /**
     * 删除提示词模版信息
     * 
     * @param id 提示词模版主键
     * @return 结果
     */
    public int deleteSysAiPromptById(Long id);
}
