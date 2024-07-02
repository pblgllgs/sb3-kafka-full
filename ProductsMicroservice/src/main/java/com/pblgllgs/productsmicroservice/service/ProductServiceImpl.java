package com.pblgllgs.productsmicroservice.service;
/*
 *
 * @author pblgl
 * Created on 26-06-2024
 *
 */

import com.pblgllgs.core.ProductCreatedEvent;
import com.pblgllgs.productsmicroservice.rest.CreateProductRestModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) throws Exception {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                productRestModel.getTitle(),
                productRestModel.getPrice(),
                productRestModel.getQuantity()
        );

        LOGGER.info("Before publishing a ProductCreatedEvent");

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord(
                "product-created-event-topic",
                productId,
                productCreatedEvent
        );

        record.headers().add("messageId",UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send(record).get();

        LOGGER.info("Partition -> {}", result.getRecordMetadata().partition());
        LOGGER.info("Topic -> {}", result.getRecordMetadata().topic());
        LOGGER.info("Offset -> {}", result.getRecordMetadata().offset());

        LOGGER.info("Returning product id: {}", productId);

        return productId;
    }
}
