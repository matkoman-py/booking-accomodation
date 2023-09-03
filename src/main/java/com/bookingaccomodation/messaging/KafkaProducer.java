package com.bookingaccomodation.messaging;

import com.bookingaccomodation.model.dto.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    public void sendMessage(NotificationDTO notificationDTO){
        LOGGER.info(String.format("Notification sent -> %s", notificationDTO));
        kafkaTemplate.send("notification-topic", notificationDTO);
    }
}
