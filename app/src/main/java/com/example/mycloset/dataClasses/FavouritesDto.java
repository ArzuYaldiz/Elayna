package com.example.mycloset.dataClasses;

public class FavouritesDto {
    private Integer combin_id;
    private Integer cloth_id;
    private Integer user_id;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getCloth_id() {
        return cloth_id;
    }

    public void setCloth_id(Integer cloth_id) {
        this.cloth_id = cloth_id;
    }

    public Integer getCombin_id() {
        return combin_id;
    }

    public void setCombin_id(Integer combin_id) {
        this.combin_id = combin_id;
    }
}
