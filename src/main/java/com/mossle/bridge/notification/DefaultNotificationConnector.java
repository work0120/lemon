package com.mossle.bridge.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mossle.api.internal.TemplateConnector;
import com.mossle.api.internal.TemplateDTO;
import com.mossle.api.notification.NotificationConnector;
import com.mossle.api.notification.NotificationDTO;
import com.mossle.api.notification.NotificationHandler;
import com.mossle.api.notification.NotificationRegistry;

import com.mossle.ext.template.TemplateService;

public class DefaultNotificationConnector implements NotificationConnector,
        NotificationRegistry {
    private Map<String, NotificationHandler> map = new HashMap<String, NotificationHandler>();
    private TemplateConnector templateConnector;
    private TemplateService templateService;

    public void send(NotificationDTO notificationDto) {
        if (notificationDto.getTemplate() != null) {
            TemplateDTO templateDto = templateConnector
                    .findByCode(notificationDto.getTemplate());
            String subject = this.processTemplate(
                    templateDto.getField("subject"), notificationDto.getData());
            String content = this.processTemplate(
                    templateDto.getField("content"), notificationDto.getData());

            if (subject != null) {
                notificationDto.setSubject(subject);
            }

            if (content != null) {
                notificationDto.setContent(content);
            }
        }

        List<String> types = notificationDto.getTypes();

        for (String type : types) {
            sendByType(type, notificationDto);
        }
    }

    public void sendByType(String type, NotificationDTO notificationDto) {
        NotificationHandler notificationHandler = map.get(type);

        if (notificationHandler == null) {
            return;
        }

        notificationHandler.handle(notificationDto);
    }

    public String processTemplate(String template, Map<String, Object> data) {
        return templateService.renderText(template, data);
    }

    public void register(NotificationHandler notificationHandler) {
        map.put(notificationHandler.getType(), notificationHandler);
    }

    public void unregister(NotificationHandler notificationHandler) {
        map.remove(notificationHandler.getType());
    }

    public Collection<String> getTypes() {
        return map.keySet();
    }

    public void setMap(Map<String, NotificationHandler> map) {
        this.map = map;
    }

    public void setTemplateConnector(TemplateConnector templateConnector) {
        this.templateConnector = templateConnector;
    }

    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }
}
