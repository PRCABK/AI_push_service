package com.ruoyi.pushtool.mapper;

import com.ruoyi.pushtool.domain.SysWecomConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysWecomConfigMapper {
    /** 查询推送平台配置 */
    SysWecomConfig selectSysWecomConfigById(Long id);
}