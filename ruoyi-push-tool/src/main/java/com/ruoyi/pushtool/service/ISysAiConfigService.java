package com.ruoyi.pushtool.service;

import java.util.List;
import com.ruoyi.pushtool.domain.SysAiConfig;

/**
 * AI平台配置Service接口
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
public interface ISysAiConfigService 
{
    /**
     * 查询AI平台配置
     * 
     * @param id AI平台配置主键
     * @return AI平台配置
     */
    public SysAiConfig selectSysAiConfigById(Long id);

    /**
     * 查询AI平台配置列表
     * 
     * @param sysAiConfig AI平台配置
     * @return AI平台配置集合
     */
    public List<SysAiConfig> selectSysAiConfigList(SysAiConfig sysAiConfig);

    /**
     * 新增AI平台配置
     * 
     * @param sysAiConfig AI平台配置
     * @return 结果
     */
    public int insertSysAiConfig(SysAiConfig sysAiConfig);

    /**
     * 修改AI平台配置
     * 
     * @param sysAiConfig AI平台配置
     * @return 结果
     */
    public int updateSysAiConfig(SysAiConfig sysAiConfig);

    /**
     * 批量删除AI平台配置
     * 
     * @param ids 需要删除的AI平台配置主键集合
     * @return 结果
     */
    public int deleteSysAiConfigByIds(String ids);

    /**
     * 删除AI平台配置信息
     * 
     * @param id AI平台配置主键
     * @return 结果
     */
    public int deleteSysAiConfigById(Long id);
}
