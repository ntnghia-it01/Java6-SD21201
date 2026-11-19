package com.fpoly.java6.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
