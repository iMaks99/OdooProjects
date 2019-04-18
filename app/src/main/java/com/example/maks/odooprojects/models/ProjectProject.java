package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ProjectProject {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("partner")
    private String partner;
    @SerializedName("tasks_count")
    private int tasksCount;
    @SerializedName("is_favourite")
    private Boolean isFavourite;
    @SerializedName("color")
    private int color;

    public ProjectProject(int id, String name, String partner, int tasksCount, Boolean isFavourite,
                          int color) {
        this.id = id;
        this.name = name;
        this.partner = partner;
        this.tasksCount = tasksCount;
        this.isFavourite = isFavourite;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartner() {
        return partner;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public int getColor() {
        return color;
    }
}
