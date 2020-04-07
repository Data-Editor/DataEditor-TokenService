package com.niek125.tokenservice.kafka;

import com.niek125.tokenservice.events.Event;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaProducer implements KafkaDispatcher {
    private final KafkaTemplate<String, Event> kafkaTemplate;

    public void dispatch(String topic, Event event) {
        kafkaTemplate.send(topic, event);
    }
}
