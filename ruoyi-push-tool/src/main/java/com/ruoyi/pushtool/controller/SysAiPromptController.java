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
import com.ruoyi.pushtool.domain.SysAiPrompt;
import com.ruoyi.pushtool.service.ISysAiPromptService;
import com.ruoyi.pushtool.service.ISysAiConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 提示词模版Controller
 * 
 * @author ruoyi
 * @date 2026-02-04
 */
@Controller
@RequestMapping("/pushtool/aiPrompt")
public class SysAiPromptController extends BaseController
{
    private static final String PERMISSION_PREFIX = "pushtool:aiPrompt";
    private final String prefix = "pushtool/aiPrompt";

    @Autowired
    private ISysAiPromptService sysAiPromptService;

    @Autowired
    private ISysAiConfigService sysAiConfigService;

    @RequiresPermissions(PERMISSION_PREFIX + ":view")
    @GetMapping()
    public String aiPrompt()
    {
        return prefix + "/aiPrompt";
    }

    /**
     * 查询提示词模版列表
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysAiPrompt sysAiPrompt)
    {
        startPage();
        List<SysAiPrompt> list = sysAiPromptService.selectSysAiPromptList(sysAiPrompt);
        return getDataTable(list);
    }

    /**
     * 导出提示词模版列表
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":export")
    @Log(title = "提示词模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysAiPrompt sysAiPrompt)
    {
        List<SysAiPrompt> list = sysAiPromptService.selectSysAiPromptList(sysAiPrompt);
        ExcelUtil<SysAiPrompt> util = new ExcelUtil<SysAiPrompt>(SysAiPrompt.class);
        return util.exportExcel(list, "提示词模版数据");
    }

    /**
     * 新增提示词模版
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":add")
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        fillAiConfigOptions(mmap);
        return prefix + "/add";
    }

    /**
     * 新增保存提示词模版
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":add")
    @Log(title = "提示词模版", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(SysAiPrompt sysAiPrompt)
    {
        return toAjax(sysAiPromptService.insertSysAiPrompt(sysAiPrompt));
    }

    /**
     * 修改提示词模版
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        SysAiPrompt sysAiPrompt = sysAiPromptService.selectSysAiPromptById(id);
        mmap.put("sysAiPrompt", sysAiPrompt);
        fillAiConfigOptions(mmap);
        return prefix + "/edit";
    }

    /**
     * 修改保存提示词模版
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":edit")
    @Log(title = "提示词模版", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysAiPrompt sysAiPrompt)
    {
        return toAjax(sysAiPromptService.updateSysAiPrompt(sysAiPrompt));
    }

    /**
     * 删除提示词模版
     */
    @RequiresPermissions(PERMISSION_PREFIX + ":remove")
    @Log(title = "提示词模版", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(sysAiPromptService.deleteSysAiPromptByIds(ids));
    }

    private void fillAiConfigOptions(ModelMap mmap)
    {
        List<SysAiConfig> aiConfigs = sysAiConfigService.selectSysAiConfigList(new SysAiConfig());
        mmap.put("aiConfigs", aiConfigs);
    }
}
