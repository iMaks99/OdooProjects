package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ProjectTask {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("date_deadline")
    private Date deadline;
    @SerializedName("email_from")
    private String emailFrom;
    @SerializedName("priority")
    private boolean isPriority;
    @SerializedName("kanban_state")
    private String kanbanState;
    @SerializedName("mail_activity_state")
    private int mailActivityState;
    @SerializedName("stage_id")
    private int stageId;

    public ProjectTask(int id, String name, Date deadline, String emailFrom, boolean isPriority, String kanbanState, int mailActivityState, int stageId) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.emailFrom = emailFrom;
        this.isPriority = isPriority;
        this.kanbanState = kanbanState;
        this.mailActivityState = mailActivityState;
        this.stageId = stageId;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public String getKanbanState() {
        return kanbanState;
    }

    public int getMailActivityState() {
        return mailActivityState;
    }

    public int getStageId() {
        return stageId;
    }
}

