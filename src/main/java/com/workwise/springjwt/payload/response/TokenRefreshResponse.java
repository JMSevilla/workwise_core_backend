package com.workwise.springjwt.payload.response;

import lombok.Data;

@Data
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenRefreshResponse(String token, String requestRefreshToken) {
        this.accessToken = token;
        this.refreshToken = requestRefreshToken;
    }
}
