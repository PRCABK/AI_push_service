package com.ruoyi.pushtool.mapper;

import com.ruoyi.pushtool.domain.SysPushTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 推送任务记录 Mapper
 */
@Mapper
public interface SysPushTaskMapper {

    /**
     * 新增推送任务
     */
    int insertSysPushTask(SysPushTask task);
}
