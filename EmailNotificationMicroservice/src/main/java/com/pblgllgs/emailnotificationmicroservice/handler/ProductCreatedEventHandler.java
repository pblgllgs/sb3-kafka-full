package com.pblgllgs.emailnotificationmicroservice.handler;
/*
 *
 * @author pblgl
 * Created on 28-06-2024
 *
 */

import com.pblgllgs.core.ProductCreatedEvent;
import com.pblgllgs.emailnotificationmicroservice.error.NotRetryableException;
import com.pblgllgs.emailnotificationmicroservice.error.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(
        topics = "product-created-event-topic"
)
public class ProductCreatedEventHandler {

    private final Logger log = LoggerFactory.getLogger(ProductCreatedEventHandler.class);

    private final RestTemplate restTemplate;

    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        log.info("Received a new message: {} -> with productId: {}", productCreatedEvent.getTitle(), productCreatedEvent.getProductId());
        String url = "http://localhost:8082/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().value() == HttpStatus.OK.value()) {
                log.info("Received response from {}", response.getBody());
            }
        } catch (ResourceAccessException ex) {
            log.error(ex.getMessage());
            throw new RetryableException(ex.getMessage());
        } catch (HttpServerErrorException ex){
            log.error(ex.getMessage());
            throw new NotRetryableException(ex.getMessage());
        } catch (Exception ex){
            log.error(ex.getMessage());
            throw new NotRetryableException(ex.getMessage());
        }
    }

}
