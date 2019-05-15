package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ProjectProject {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("partner_id")
    private int partnerId;
    @SerializedName("partner_name")
    private String partner;
    @SerializedName("tasks_count")
    private int tasksCount;
    @SerializedName("is_favourite")
    private Boolean isFavourite;
    @SerializedName("color")
    private int color;
    @SerializedName("label_tasks")
    private String tasksLabel;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("privacy_visibility")
    private String privacyVisibility;

    public ProjectProject() { }

    public ProjectProject(int id, String name, int partnerId, String partner, int tasksCount, Boolean isFavourite,
                          int color, String tasksLabel, int userId, String userName, String privacyVisibility) {
        this.id = id;
        this.name = name;
        this.partnerId = partnerId;
        this.partner = partner;
        this.tasksCount = tasksCount;
        this.isFavourite = isFavourite;
        this.color = color;
        this.tasksLabel = tasksLabel;
        this.userId = userId;
        this.userName = userName;
        this.privacyVisibility = privacyVisibility;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public void setColor(int color) {
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

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getTasksLabel() {
        return tasksLabel;
    }

    public void setTasksLabel(String tasksLabel) {
        this.tasksLabel = tasksLabel;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPrivacyVisibility() {
        return privacyVisibility;
    }

    public void setPrivacyVisibility(String privacyVisibility) {
        this.privacyVisibility = privacyVisibility;
    }
}
