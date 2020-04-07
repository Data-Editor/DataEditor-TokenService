package com.niek125.tokenservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niek125.tokenservice.events.Event;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

public class KafkaSerializer implements Serializer<Event> {
    private final ObjectMapper objectMapper;

    public KafkaSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @SneakyThrows
    public byte[] serialize(String s, Event event) {
        final StringBuilder sb = new StringBuilder(event.getClass().getSimpleName());
        sb.append(".");
        sb.append(objectMapper.writeValueAsString(event));
        return sb.toString().getBytes();
    }
}
