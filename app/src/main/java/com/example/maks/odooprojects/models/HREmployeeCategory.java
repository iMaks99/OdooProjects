package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class HREmployeeCategory {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private int color;

    public HREmployeeCategory(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
