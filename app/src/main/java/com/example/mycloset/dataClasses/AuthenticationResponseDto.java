package com.example.mycloset.dataClasses;

public class AuthenticationResponseDto {
    private String token;
    private String error;
    private String userError;

    public AuthenticationResponseDto(String token, String error, String userError) {
        this.token = token;
        this.error = error;
        this.userError = userError;
    }
    public String getToken() {
        return token;
    }

    public String getUserError() {
        return userError;
    }
}
