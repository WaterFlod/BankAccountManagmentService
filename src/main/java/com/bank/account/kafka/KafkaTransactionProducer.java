package com.bank.account.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("{app.kafka.topic}")
    private String topic;

    public void sendTransactionEvent(TransactionEvent event) {
        kafkaTemplate.send(topic, event.getAccountNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Transaction event sent to Kafka: {}", event.getTransactionId());
                    } else {
                        log.error("Failed to send transaction event: {}", ex.getMessage());
                    }
                });
    }
}
