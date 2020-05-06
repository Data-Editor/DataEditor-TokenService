package com.niek125.tokenservice.token;

public interface TokenBuilder {
    String getNewToken(String uid, String userName, String pfp, String permissions);
}
