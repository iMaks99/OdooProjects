package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MailActivity {
    @SerializedName("id")
    private int id;
    @SerializedName("res_id")
    private int resId;
    @SerializedName("res_model")
    private String resModel;
    @SerializedName("res_name")
    private String resName;
    @SerializedName("activity_type")
    private String activityType;
    @SerializedName("summary")
    private String summary;
    @SerializedName("note")
    private String note;
    @SerializedName("date_deadline")
    private Date dateDeadline;

    public MailActivity(int id, int resId, String resModel, String resName, String activityType,
                        String summary, String note, Date dateDeadline) {
        this.id = id;
        this.resId = resId;
        this.resModel = resModel;
        this.resName = resName;
        this.activityType = activityType;
        this.summary = summary;
        this.note = note;
        this.dateDeadline = dateDeadline;
    }

    public int getId() {
        return id;
    }

    public int getResId() {
        return resId;
    }

    public String getResModel() {
        return resModel;
    }

    public String getResName() {
        return resName;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getSummary() {
        return summary;
    }

    public String getNote() {
        return note;
    }

    public Date getDateDeadline() {
        return dateDeadline;
    }
}
