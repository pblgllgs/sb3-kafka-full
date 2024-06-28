package com.pblgllgs.emailnotificationmicroservice.handler;
/*
 *
 * @author pblgl
 * Created on 28-06-2024
 *
 */

import com.pblgllgs.core.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = "product-created-event-topic"
)
public class ProductCreatedEventHandler {

    private final Logger log = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent){
        log.info("Received a new message: {}", productCreatedEvent.getTitle());
    }
}