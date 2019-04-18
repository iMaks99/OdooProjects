package com.example.maks.odooprojects.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class ProjectTask {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("kanban_state")
    private String kanbanState;
    @SerializedName("email_from")
    private String emailFrom;
    @SerializedName("priority")
    private boolean isPriority;
    @SerializedName("date_deadline")
    private Date deadline;
    @SerializedName("mail_activity_state")
    private int mailActivityState;
    @SerializedName("stage_id")
    private int stageId;
    @SerializedName("description")
    private String description;
    @SerializedName("project_name")
    private String projectName;
    @SerializedName("planned_hours")
    private int plannedHours;
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("tags")
    private List<ProjectTaskTag> tags;

    public ProjectTask(int id, String name, Date deadline, String emailFrom, boolean isPriority,
                       String kanbanState, int mailActivityState, int stageId, String description,
                       String projectName, int plannedHours, String assignedTo, List<ProjectTaskTag> tags) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
        this.emailFrom = emailFrom;
        this.isPriority = isPriority;
        this.kanbanState = kanbanState;
        this.mailActivityState = mailActivityState;
        this.stageId = stageId;
        this.description = description;
        this.projectName = projectName;
        this.plannedHours = plannedHours;
        this.assignedTo = assignedTo;
        this.tags = tags;
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

    public String getDescription() {
        return description;
    }

    public String getProjectName() {
        return projectName;
    }

    public int getPlannedHours() {
        return plannedHours;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public List<ProjectTaskTag> getTags() {
        return tags;
    }
}

