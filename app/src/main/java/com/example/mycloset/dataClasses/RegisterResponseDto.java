package com.example.mycloset.dataClasses;


public class RegisterResponseDto {
    private String status;
    private String body;
    private int id;
    public RegisterResponseDto(String status, String body, int id) {
        this.status = status;
        this.body = body;
        this.id = id;
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

    public int getId() {return id;}

}
