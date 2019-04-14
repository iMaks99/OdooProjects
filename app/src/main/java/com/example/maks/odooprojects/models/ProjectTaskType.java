package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ProjectTaskType {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public ProjectTaskType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
