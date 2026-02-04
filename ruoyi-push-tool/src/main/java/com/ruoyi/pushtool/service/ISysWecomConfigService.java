package com.ruoyi.pushtool.service;

import java.util.List;
import com.ruoyi.pushtool.domain.SysWecomConfig;

/**
 * 推送平台配置Service接口
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
public interface ISysWecomConfigService 
{
    /**
     * 查询推送平台配置
     * 
     * @param id 推送平台配置主键
     * @return 推送平台配置
     */
    public SysWecomConfig selectSysWecomConfigById(Long id);

    /**
     * 查询推送平台配置列表
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 推送平台配置集合
     */
    public List<SysWecomConfig> selectSysWecomConfigList(SysWecomConfig sysWecomConfig);

    /**
     * 新增推送平台配置
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 结果
     */
    public int insertSysWecomConfig(SysWecomConfig sysWecomConfig);

    /**
     * 修改推送平台配置
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 结果
     */
    public int updateSysWecomConfig(SysWecomConfig sysWecomConfig);

    /**
     * 批量删除推送平台配置
     * 
     * @param ids 需要删除的推送平台配置主键集合
     * @return 结果
     */
    public int deleteSysWecomConfigByIds(String ids);

    /**
     * 删除推送平台配置信息
     * 
     * @param id 推送平台配置主键
     * @return 结果
     */
    public int deleteSysWecomConfigById(Long id);
}
