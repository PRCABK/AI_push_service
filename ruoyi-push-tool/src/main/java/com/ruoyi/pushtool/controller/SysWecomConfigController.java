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
import com.ruoyi.pushtool.domain.SysWecomConfig;
import com.ruoyi.pushtool.service.ISysWecomConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 推送平台配置Controller
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Controller
@RequestMapping("/pushtool/config")
public class SysWecomConfigController extends BaseController
{
    private String prefix = "pushtool/config";

    @Autowired
    private ISysWecomConfigService sysWecomConfigService;

    @RequiresPermissions("system:config:view")
    @GetMapping()
    public String config()
    {
        return prefix + "/config";
    }

    /**
     * 查询推送平台配置列表
     */
    @RequiresPermissions("system:config:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysWecomConfig sysWecomConfig)
    {
        startPage();
        List<SysWecomConfig> list = sysWecomConfigService.selectSysWecomConfigList(sysWecomConfig);
        return getDataTable(list);
    }

    /**
     * 导出推送平台配置列表
     */
    @RequiresPermissions("system:config:export")
    @Log(title = "推送平台配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysWecomConfig sysWecomConfig)
    {
        List<SysWecomConfig> list = sysWecomConfigService.selectSysWecomConfigList(sysWecomConfig);
        ExcelUtil<SysWecomConfig> util = new ExcelUtil<SysWecomConfig>(SysWecomConfig.class);
        return util.exportExcel(list, "推送平台配置数据");
    }

    /**
     * 新增推送平台配置
     */
    @RequiresPermissions("system:config:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存推送平台配置
     */
    @RequiresPermissions("system:config:add")
    @Log(title = "推送平台配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysWecomConfig sysWecomConfig)
    {
        return toAjax(sysWecomConfigService.insertSysWecomConfig(sysWecomConfig));
    }

    /**
     * 修改推送平台配置
     */
    @RequiresPermissions("system:config:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        SysWecomConfig sysWecomConfig = sysWecomConfigService.selectSysWecomConfigById(id);
        mmap.put("sysWecomConfig", sysWecomConfig);
        return prefix + "/edit";
    }

    /**
     * 修改保存推送平台配置
     */
    @RequiresPermissions("system:config:edit")
    @Log(title = "推送平台配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysWecomConfig sysWecomConfig)
    {
        return toAjax(sysWecomConfigService.updateSysWecomConfig(sysWecomConfig));
    }

    /**
     * 删除推送平台配置
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "推送平台配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sysWecomConfigService.deleteSysWecomConfigByIds(ids));
    }
}
