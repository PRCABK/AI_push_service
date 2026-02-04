package com.ruoyi.pushtool.controller;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pushtool.domain.SysAiConfig;
import com.ruoyi.pushtool.service.ISysAiConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * AI平台配置Controller
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Controller
@RequestMapping("/pushtool/aiConfig")
public class SysAiConfigController extends BaseController
{
    private String prefix = "pushtool/aiConfig";

    @Autowired
    private ISysAiConfigService sysAiConfigService;

    @RequiresPermissions("system:config:view")
    @GetMapping()
    public String config()
    {
        return prefix + "/config";
    }

    /**
     * 查询AI平台配置列表
     */
    @RequiresPermissions("system:config:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysAiConfig sysAiConfig)
    {
        startPage();
        List<SysAiConfig> list = sysAiConfigService.selectSysAiConfigList(sysAiConfig);
        return getDataTable(list);
    }

    /**
     * 导出AI平台配置列表
     */
    @RequiresPermissions("system:config:export")
    @Log(title = "AI平台配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysAiConfig sysAiConfig)
    {
        List<SysAiConfig> list = sysAiConfigService.selectSysAiConfigList(sysAiConfig);
        ExcelUtil<SysAiConfig> util = new ExcelUtil<SysAiConfig>(SysAiConfig.class);
        return util.exportExcel(list, "AI平台配置数据");
    }

    /**
     * 新增AI平台配置
     */
    @RequiresPermissions("system:config:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存AI平台配置
     */
    @RequiresPermissions("system:config:add")
    @Log(title = "AI平台配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysAiConfig sysAiConfig)
    {
        return toAjax(sysAiConfigService.insertSysAiConfig(sysAiConfig));
    }

    /**
     * 修改AI平台配置
     */
    @RequiresPermissions("system:config:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        SysAiConfig sysAiConfig = sysAiConfigService.selectSysAiConfigById(id);
        mmap.put("sysAiConfig", sysAiConfig);
        return prefix + "/edit";
    }

    /**
     * 修改保存AI平台配置
     */
    @RequiresPermissions("system:config:edit")
    @Log(title = "AI平台配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysAiConfig sysAiConfig)
    {
        return toAjax(sysAiConfigService.updateSysAiConfig(sysAiConfig));
    }

    /**
     * 删除AI平台配置
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "AI平台配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sysAiConfigService.deleteSysAiConfigByIds(ids));
    }
}
