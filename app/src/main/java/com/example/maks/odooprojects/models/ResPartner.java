package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

public class ResPartner {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("display_name")
    private String displayedName;
    @SerializedName("email")
    private String email;

    public ResPartner(int id, String name, String displayedName, String email) {
        this.id = id;
        this.name = name;
        this.displayedName = displayedName;
        this.email = email;
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

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
