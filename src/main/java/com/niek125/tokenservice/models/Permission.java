package com.niek125.tokenservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Permission {
    GrantStatus grantStatus;
    String projectId;

    public Map<String, String> toMap(){
        final Map<String, String> map = new HashMap<>();
        map.put("projectId",projectId);
        map.put("grantStatus",grantStatus.toString());
        return map;
    }
}
