package com.yookos.notifyserver.services;

import com.yookos.notifyserver.config.RabbitConfig;
import com.yookos.notifyserver.rest.domain.Notification;
import com.yookos.notifyserver.rest.domain.NotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jome on 2014/08/11.
 */

@Component
public class NotificationSender {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RabbitTemplate template;

    public void sendNotification(NotificationResource notification) {
        template.setExchange("yookos.notifications");
        template.convertAndSend(RabbitConfig.notificationQueue, notification);
        log.debug("Message sent: {}", notification.toString());
    }

    public void sendPFNotification(Notification notification) {
        template.setExchange("yookos.notifications");
        template.convertAndSend("yookos.pcl.notifications", notification);
        log.debug("Message sent: {}", notification.toString());
    }
}
