package com.niek125.tokenservice.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.niek125.tokenservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoggedInEvent extends Event {
    private User user;

    public UserLoggedInEvent(String origin, User user){
        super(origin);
        this.user = user;
    }
}
