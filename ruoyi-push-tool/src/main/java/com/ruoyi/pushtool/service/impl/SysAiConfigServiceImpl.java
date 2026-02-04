package com.ruoyi.pushtool.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pushtool.mapper.SysAiConfigMapper;
import com.ruoyi.pushtool.domain.SysAiConfig;
import com.ruoyi.pushtool.service.ISysAiConfigService;
import com.ruoyi.common.core.text.Convert;

/**
 * AI平台配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Service
public class SysAiConfigServiceImpl implements ISysAiConfigService 
{
    @Autowired
    private SysAiConfigMapper sysAiConfigMapper;

    /**
     * 查询AI平台配置
     * 
     * @param id AI平台配置主键
     * @return AI平台配置
     */
    @Override
    public SysAiConfig selectSysAiConfigById(Long id)
    {
        return sysAiConfigMapper.selectSysAiConfigById(id);
    }

    /**
     * 查询AI平台配置列表
     * 
     * @param sysAiConfig AI平台配置
     * @return AI平台配置
     */
    @Override
    public List<SysAiConfig> selectSysAiConfigList(SysAiConfig sysAiConfig)
    {
        return sysAiConfigMapper.selectSysAiConfigList(sysAiConfig);
    }

    /**
     * 新增AI平台配置
     * 
     * @param sysAiConfig AI平台配置
     * @return 结果
     */
    @Override
    public int insertSysAiConfig(SysAiConfig sysAiConfig)
    {
        return sysAiConfigMapper.insertSysAiConfig(sysAiConfig);
    }

    /**
     * 修改AI平台配置
     * 
     * @param sysAiConfig AI平台配置
     * @return 结果
     */
    @Override
    public int updateSysAiConfig(SysAiConfig sysAiConfig)
    {
        return sysAiConfigMapper.updateSysAiConfig(sysAiConfig);
    }

    /**
     * 批量删除AI平台配置
     * 
     * @param ids 需要删除的AI平台配置主键
     * @return 结果
     */
    @Override
    public int deleteSysAiConfigByIds(String ids)
    {
        return sysAiConfigMapper.deleteSysAiConfigByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除AI平台配置信息
     * 
     * @param id AI平台配置主键
     * @return 结果
     */
    @Override
    public int deleteSysAiConfigById(Long id)
    {
        return sysAiConfigMapper.deleteSysAiConfigById(id);
    }
}
