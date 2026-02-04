package com.ruoyi.pushtool.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pushtool.mapper.SysAiPromptMapper;
import com.ruoyi.pushtool.domain.SysAiPrompt;
import com.ruoyi.pushtool.service.ISysAiPromptService;
import com.ruoyi.common.core.text.Convert;

/**
 * 提示词模版Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Service
public class SysAiPromptServiceImpl implements ISysAiPromptService 
{
    @Autowired
    private SysAiPromptMapper sysAiPromptMapper;

    /**
     * 查询提示词模版
     * 
     * @param id 提示词模版主键
     * @return 提示词模版
     */
    @Override
    public SysAiPrompt selectSysAiPromptById(Long id)
    {
        return sysAiPromptMapper.selectSysAiPromptById(id);
    }

    /**
     * 查询提示词模版列表
     * 
     * @param sysAiPrompt 提示词模版
     * @return 提示词模版
     */
    @Override
    public List<SysAiPrompt> selectSysAiPromptList(SysAiPrompt sysAiPrompt)
    {
        return sysAiPromptMapper.selectSysAiPromptList(sysAiPrompt);
    }

    /**
     * 新增提示词模版
     * 
     * @param sysAiPrompt 提示词模版
     * @return 结果
     */
    @Override
    public int insertSysAiPrompt(SysAiPrompt sysAiPrompt)
    {
        return sysAiPromptMapper.insertSysAiPrompt(sysAiPrompt);
    }

    /**
     * 修改提示词模版
     * 
     * @param sysAiPrompt 提示词模版
     * @return 结果
     */
    @Override
    public int updateSysAiPrompt(SysAiPrompt sysAiPrompt)
    {
        return sysAiPromptMapper.updateSysAiPrompt(sysAiPrompt);
    }

    /**
     * 批量删除提示词模版
     * 
     * @param ids 需要删除的提示词模版主键
     * @return 结果
     */
    @Override
    public int deleteSysAiPromptByIds(String ids)
    {
        return sysAiPromptMapper.deleteSysAiPromptByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除提示词模版信息
     * 
     * @param id 提示词模版主键
     * @return 结果
     */
    @Override
    public int deleteSysAiPromptById(Long id)
    {
        return sysAiPromptMapper.deleteSysAiPromptById(id);
    }
}
