package com.example.mycloset.dataClasses;

public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;

    public RegisterRequestDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
