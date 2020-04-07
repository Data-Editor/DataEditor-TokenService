package com.niek125.tokenservice.kafka;

import com.niek125.tokenservice.events.Event;

public interface KafkaDispatcher {
    void dispatch(String topic, Event event);
}
