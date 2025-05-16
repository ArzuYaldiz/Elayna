package com.example.mycloset.dataClasses;

import Enums.HairType;

public class UpdateUserDto {
    private int id;
    private String image_url;
    private float height;
    private float weight;
    private long phone_number;
    private int age;
    private HairType hair_color;

    public UpdateUserDto(int id, String image_url, float height, float weight, long phone_number, int age, HairType hair_color) {
        this.id = id;
        this.image_url = image_url;
        this.height = height;
        this.weight = weight;
        this.phone_number = phone_number;
        this.age = age;
        this.hair_color = hair_color;
    }

    public int getId(){return id;}

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url() {
        this.image_url = image_url;
    }

    public float getHeight() {
        return height;
    }


    public float getWeight() {
        return weight;
    }



    public int getAge() {
        return age;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public HairType getHair_color() {
        return hair_color;
    }
}
