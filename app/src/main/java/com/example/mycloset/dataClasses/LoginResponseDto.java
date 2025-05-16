package com.example.mycloset.dataClasses;

public class LoginResponseDto {

    private String status;
    private String body;
    private int verified;
    private int age;


    public LoginResponseDto(String status, String body, int verified, int age) {
        this.status = status;
        this.body = body;
        this.verified = verified;
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getVerified(){
        return verified;
    }
    public int getAge(){
        return age;
    }
}
