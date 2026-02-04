package com.ruoyi.pushtool.service;

import com.ruoyi.pushtool.domain.dto.PushRequestDTO;

/**
 * 推送工具服务接口
 */
public interface IPushService {
    /** 执行分析与推送任务 */
    void executePush(PushRequestDTO request);
}