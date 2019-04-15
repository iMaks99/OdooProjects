package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class HRDepartment {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("complete_name")
    private String completeName;

    public HRDepartment(int id, String name, String completeName) {
        this.id = id;
        this.name = name;
        this.completeName = completeName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompleteName() {
        return completeName;
    }
}
