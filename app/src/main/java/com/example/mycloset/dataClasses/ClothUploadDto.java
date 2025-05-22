package com.example.mycloset.dataClasses;

public class ClothUploadDto {
    private int id;
    private String Image_url;

    public ClothUploadDto(int id, String Image_url) {
        this.id = id;
        this.Image_url = Image_url;
    }

    public String getImage_url() {
        return Image_url;
    }

    public void setImage_url(String Image_url) {
        this.Image_url = Image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}