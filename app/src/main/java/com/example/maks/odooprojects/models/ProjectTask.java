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
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("customer_display_name")
    private String customerDisplayName;
    @SerializedName("customer_email")
    private String customerEmail;
    @SerializedName("priority")
    private int isPriority;
    @SerializedName("date_deadline")
    private Date deadline;
    @SerializedName("mail_activity_state")
    private int mailActivityState;
    @SerializedName("stage_id")
    private int stageId;
    @SerializedName("description")
    private String description;
    @SerializedName("project_id")
    private int projectId;
    @SerializedName("project_name")
    private String projectName;
    @SerializedName("planned_hours")
    private int plannedHours;
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("assigned_to_id")
    private int assignedToId;
    @SerializedName("tags")
    private List<ProjectTaskTag> tags;
    @SerializedName("color")
    private int color;

    public ProjectTask() {
    }

    public ProjectTask(int id, String name, String customerName, Date deadline, int isPriority,
                       String kanbanState, int customerId, String customerEmail, int mailActivityState, int stageId, String description,
                       int projectId, String projectName, int plannedHours, String assignedTo, int assignedToId, List<ProjectTaskTag> tags,
                       int color) {
        this.id = id;
        this.name = name;
        this.customerDisplayName = customerName;
        this.deadline = deadline;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.isPriority = isPriority;
        this.kanbanState = kanbanState;
        this.mailActivityState = mailActivityState;
        this.stageId = stageId;
        this.description = description;
        this.projectId = projectId;
        this.projectName = projectName;
        this.plannedHours = plannedHours;
        this.assignedTo = assignedTo;
        this.assignedToId = assignedToId;
        this.tags = tags;
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKanbanState(String kanbanState) {
        this.kanbanState = kanbanState;
    }


    public void setIsPriority(int isPriority) {
        this.isPriority = isPriority;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setMailActivityState(int mailActivityState) {
        this.mailActivityState = mailActivityState;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setPlannedHours(int plannedHours) {
        this.plannedHours = plannedHours;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setAssignedToId(int assignedToId) {
        this.assignedToId = assignedToId;
    }

    public void setTags(List<ProjectTaskTag> tags) {
        this.tags = tags;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerDisplayName(String customerDisplayName) {
        this.customerDisplayName = customerDisplayName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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

    public int isPriority() {
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

    public int getColor() {
        return color;
    }

    public int getAssignedToId() {
        return assignedToId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerDisplayName() {
        return customerDisplayName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getProjectId() {
        return projectId;
    }
}

