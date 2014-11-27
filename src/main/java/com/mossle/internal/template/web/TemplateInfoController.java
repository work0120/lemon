package com.mossle.internal.template.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.internal.template.persistence.domain.TemplateInfo;
import com.mossle.internal.template.persistence.manager.TemplateInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/template")
public class TemplateInfoController {
    private TemplateInfoManager templateInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("template-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = templateInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "template/template-info-list";
    }

    @RequestMapping("template-info-input")
    public String input(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "type", required = false) String whitelistTypeCode,
            Model model) {
        TemplateInfo templateInfo = null;

        if (id != null) {
            templateInfo = templateInfoManager.get(id);
            model.addAttribute("model", templateInfo);
        }

        return "template/template-info-input";
    }

    @RequestMapping("template-info-save")
    public String save(@ModelAttribute TemplateInfo templateInfo,
            RedirectAttributes redirectAttributes) {
        Long id = templateInfo.getId();
        TemplateInfo dest = null;

        if (id != null) {
            dest = templateInfoManager.get(id);
            beanMapper.copy(templateInfo, dest);
        } else {
            dest = templateInfo;
        }

        templateInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/template/template-info-list.do";
    }

    @RequestMapping("template-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<TemplateInfo> templateInfos = templateInfoManager
                .findByIds(selectedItem);

        for (TemplateInfo templateInfo : templateInfos) {
            templateInfoManager.remove(templateInfo);
        }

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/template/template-info-list.do";
    }

    // ~ ======================================================================
    @Resource
    public void setTemplateInfoManager(TemplateInfoManager templateInfoManager) {
        this.templateInfoManager = templateInfoManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
