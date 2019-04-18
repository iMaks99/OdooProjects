package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ProjectTaskTag {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private int color;

    public ProjectTaskTag(int id, String name, int color) {
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
