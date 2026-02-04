package com.ruoyi.pushtool.mapper;

import com.ruoyi.pushtool.domain.SysAiConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SysAiConfigMapper {
    /** 查询AI平台配置 */
    SysAiConfig selectSysAiConfigById(Long id);
}