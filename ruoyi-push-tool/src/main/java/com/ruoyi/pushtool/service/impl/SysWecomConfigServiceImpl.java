package com.ruoyi.pushtool.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pushtool.mapper.SysWecomConfigMapper;
import com.ruoyi.pushtool.domain.SysWecomConfig;
import com.ruoyi.pushtool.service.ISysWecomConfigService;
import com.ruoyi.common.core.text.Convert;

/**
 * 推送平台配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Service
public class SysWecomConfigServiceImpl implements ISysWecomConfigService 
{
    @Autowired
    private SysWecomConfigMapper sysWecomConfigMapper;

    /**
     * 查询推送平台配置
     * 
     * @param id 推送平台配置主键
     * @return 推送平台配置
     */
    @Override
    public SysWecomConfig selectSysWecomConfigById(Long id)
    {
        return sysWecomConfigMapper.selectSysWecomConfigById(id);
    }

    /**
     * 查询推送平台配置列表
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 推送平台配置
     */
    @Override
    public List<SysWecomConfig> selectSysWecomConfigList(SysWecomConfig sysWecomConfig)
    {
        return sysWecomConfigMapper.selectSysWecomConfigList(sysWecomConfig);
    }

    /**
     * 新增推送平台配置
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 结果
     */
    @Override
    public int insertSysWecomConfig(SysWecomConfig sysWecomConfig)
    {
        return sysWecomConfigMapper.insertSysWecomConfig(sysWecomConfig);
    }

    /**
     * 修改推送平台配置
     * 
     * @param sysWecomConfig 推送平台配置
     * @return 结果
     */
    @Override
    public int updateSysWecomConfig(SysWecomConfig sysWecomConfig)
    {
        return sysWecomConfigMapper.updateSysWecomConfig(sysWecomConfig);
    }

    /**
     * 批量删除推送平台配置
     * 
     * @param ids 需要删除的推送平台配置主键
     * @return 结果
     */
    @Override
    public int deleteSysWecomConfigByIds(String ids)
    {
        return sysWecomConfigMapper.deleteSysWecomConfigByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除推送平台配置信息
     * 
     * @param id 推送平台配置主键
     * @return 结果
     */
    @Override
    public int deleteSysWecomConfigById(Long id)
    {
        return sysWecomConfigMapper.deleteSysWecomConfigById(id);
    }
}
