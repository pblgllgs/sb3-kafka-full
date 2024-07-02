package com.pblgllgs.emailnotificationmicroservice.handler;
/*
 *
 * @author pblgl
 * Created on 28-06-2024
 *
 */

import com.pblgllgs.core.ProductCreatedEvent;
import com.pblgllgs.emailnotificationmicroservice.entity.ProcessedEventEntity;
import com.pblgllgs.emailnotificationmicroservice.error.NotRetryableException;
import com.pblgllgs.emailnotificationmicroservice.error.RetryableException;
import com.pblgllgs.emailnotificationmicroservice.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@KafkaListener(
        topics = "product-created-event-topic"
)
public class ProductCreatedEventHandler {

    private final Logger log = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    private final RestTemplate restTemplate;

    private final ProcessedEventRepository processedEventRepository;

    public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository) {
        this.restTemplate = restTemplate;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaHandler
    @Transactional
    public void handle(
            @Payload ProductCreatedEvent productCreatedEvent,
            @Header("messageId") String messageId,
            @Header(KafkaHeaders.RECEIVED_KEY) String messageKey
    ) {
        log.info(
                "Received a new message: {} -> with productId: {}",
                productCreatedEvent.getTitle(),
                productCreatedEvent.getProductId()
        );

        Optional<ProcessedEventEntity> byMessageId = processedEventRepository.findByMessageId(messageId);
        if (byMessageId.isPresent()) {
            log.error("Message id {} already exists", messageId);
            return;
        }

        String url = "http://localhost:8082/response/200";
        try {
            ResponseEntity<String> response = restTemplate
                    .exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            String.class
                    );
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Received response from {}", response.getBody());
            }
        } catch (ResourceAccessException ex) {
            log.error(ex.getMessage());
            throw new RetryableException(ex.getMessage());
        } catch (HttpServerErrorException ex) {
            log.error(ex.getMessage());
            throw new NotRetryableException(ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotRetryableException(ex.getMessage());
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId,productCreatedEvent.getProductId()));
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }
    }

}
