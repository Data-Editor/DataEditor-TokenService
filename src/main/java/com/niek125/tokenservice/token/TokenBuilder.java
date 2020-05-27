package com.niek125.tokenservice.token;

import com.niek125.tokenservice.models.Permission;

public interface TokenBuilder {
    String getNewToken(String uid, String userName, String pfp, Permission[] permissions);
}
