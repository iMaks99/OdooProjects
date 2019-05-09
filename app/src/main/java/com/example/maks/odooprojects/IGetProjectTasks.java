package com.example.maks.odooprojects;

import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskType;

import java.util.List;

public interface IGetProjectTasks {
    List<ProjectTask> returnProjectTask();
    List<ProjectTaskType> returnProjectTaskType();
}
