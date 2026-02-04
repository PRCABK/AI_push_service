package com.ruoyi.pushtool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pushtool.domain.dto.PushRequestDTO;
import com.ruoyi.pushtool.service.IPushService;


@RestController
@RequestMapping("/push-tool/api")
public class PushController extends BaseController {

    @Autowired
    private IPushService pushService;

    /**
     * 通用推送接口
     */
    @Log(title = "AI推送工具", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody PushRequestDTO request) {
        pushService.executePush(request);
        return success("推送指令已发出");
    }
}
