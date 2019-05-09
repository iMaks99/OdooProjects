package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ProjectTaskType {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("legend_blocked")
    private String legendBlocked;
    @SerializedName("legend_done")
    private String legendDone;
    @SerializedName("legend_normal")
    private String legendNormal;

    public ProjectTaskType(int id, String name, String legendBlocked, String legendDone, String legendNormal) {
        this.id = id;
        this.name = name;
        this.legendBlocked = legendBlocked;
        this.legendDone = legendDone;
        this.legendNormal = legendNormal;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLegendNormal() {
        return legendNormal;
    }

    public String getLegendDone() {
        return legendDone;
    }

    public String getLegendBlocked() {
        return legendBlocked;
    }
}
