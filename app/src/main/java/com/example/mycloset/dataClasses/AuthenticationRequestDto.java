package com.example.mycloset.dataClasses;

public class AuthenticationRequestDto {
    private String email;
    private String password;

    public AuthenticationRequestDto(String email, String password) {
        this.password = password;
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
