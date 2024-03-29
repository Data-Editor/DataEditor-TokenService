package com.niek125.tokenservice.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Event {
    private String creator;

    public Event() {
        this.creator = "token-service";
    }
}
