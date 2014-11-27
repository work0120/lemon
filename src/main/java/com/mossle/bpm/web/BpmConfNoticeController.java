package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.api.internal.TemplateConnector;
import com.mossle.api.internal.TemplateDTO;
import com.mossle.api.notification.NotificationConnector;

import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.domain.BpmConfNotice;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfNoticeManager;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("bpm")
public class BpmConfNoticeController {
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfNoticeManager bpmConfNoticeManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private BeanMapper beanMapper = new BeanMapper();
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;
    private TemplateConnector templateConnector;
    private NotificationConnector notificationConnector;

    @RequestMapping("bpm-conf-notice-list")
    public String list(@RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            Model model) {
        BpmConfNode bpmConfNode = bpmConfNodeManager.get(bpmConfNodeId);
        Long bpmConfBaseId = bpmConfNode.getBpmConfBase().getId();
        List<BpmConfNotice> bpmConfNotices = bpmConfNoticeManager.findBy(
                "bpmConfNode", bpmConfNode);
        List<BpmMailTemplate> bpmMailTemplates = bpmMailTemplateManager
                .getAll();

        model.addAttribute("bpmConfBaseId", bpmConfBaseId);
        model.addAttribute("bpmConfNotices", bpmConfNotices);
        model.addAttribute("bpmMailTemplates", bpmMailTemplates);

        return "bpm/bpm-conf-notice-list";
    }

    @RequestMapping("bpm-conf-notice-input")
    public String input(Model model) {
        List<TemplateDTO> templateDtos = templateConnector.findAll();
        model.addAttribute("templateDtos", templateDtos);

        Collection<String> types = notificationConnector.getTypes();
        model.addAttribute("types", types);

        return "bpm/bpm-conf-notice-input";
    }

    @RequestMapping("bpm-conf-notice-save")
    public String save(@ModelAttribute BpmConfNotice bpmConfNotice,
            @RequestParam("bpmConfNodeId") Long bpmConfNodeId,
            @RequestParam("templateCode") String templateCode,
            @RequestParam("notificationTypes") List<String> notificationTypes) {
        bpmConfNotice.setBpmConfNode(bpmConfNodeManager.get(bpmConfNodeId));
        bpmConfNotice.setTemplateCode(templateCode);
        bpmConfNotice.setNotificationType(join(notificationTypes));
        bpmConfNoticeManager.save(bpmConfNotice);

        return "redirect:/bpm/bpm-conf-notice-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    public String join(List<String> notificationTypes) {
        if (notificationTypes.isEmpty()) {
            return "";
        }

        StringBuilder buff = new StringBuilder();

        for (String text : notificationTypes) {
            buff.append(text).append(",");
        }

        buff.deleteCharAt(buff.length() - 1);

        return buff.toString();
    }

    @RequestMapping("bpm-conf-notice-remove")
    public String remove(@RequestParam("id") Long id) {
        BpmConfNotice bpmConfNotice = bpmConfNoticeManager.get(id);
        Long bpmConfNodeId = bpmConfNotice.getBpmConfNode().getId();
        bpmConfNoticeManager.remove(bpmConfNotice);

        return "redirect:/bpm/bpm-conf-notice-list.do?bpmConfNodeId="
                + bpmConfNodeId;
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfNoticeManager(
            BpmConfNoticeManager bpmConfNoticeManager) {
        this.bpmConfNoticeManager = bpmConfNoticeManager;
    }

    @Resource
    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    @Resource
    public void setBpmMailTemplateManager(
            BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
    }

    @Resource
    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Resource
    public void setTemplateConnector(TemplateConnector templateConnector) {
        this.templateConnector = templateConnector;
    }

    @Resource
    public void setNotificationConnector(
            NotificationConnector notificationConnector) {
        this.notificationConnector = notificationConnector;
    }
}
