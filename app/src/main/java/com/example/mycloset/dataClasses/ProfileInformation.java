package com.example.mycloset.dataClasses;

import Enums.HairType;

public class ProfileInformation {
    private int id;
    private String username;
    private int age;
    private float weight;
    private float height;
    private HairType hairType;
    private long phone_number;
    private String email;

    public ProfileInformation(String username, int age, float height, float weight, HairType hairType, long phone_number, String email){
        this.age = age;
        this.username = username;
        this.height = height;
        this.weight = weight;
        this.hairType = hairType;
        this.phone_number = phone_number;
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public float getHeight() {
        return height;
    }

    public HairType getHairType() {
        return hairType;
    }

    public String getEmail() {
        return email;
    }


    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHairType(HairType hairType) {
        this.hairType = hairType;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

}
