package com.example.mycloset.dataClasses;

// Kıyafet model sınıfı
public class ClothingItem {
    private String name;
    private int imageResId;

    public ClothingItem(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
