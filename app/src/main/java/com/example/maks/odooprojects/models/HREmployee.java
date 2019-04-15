package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HREmployee {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("work_location")
    private String workLocation;
    @SerializedName("job")
    private String job;
    @SerializedName("category")
    private List<HREmployeeCategory> categories;

    public HREmployee(int id, String name, String workLocation, String job, List<HREmployeeCategory> categories) {
        this.id = id;
        this.name = name;
        this.workLocation = workLocation;
        this.job = job;
        this.categories = categories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public String getJob() {
        return job;
    }

    public List<HREmployeeCategory> getCategories() {
        return categories;
    }
}
