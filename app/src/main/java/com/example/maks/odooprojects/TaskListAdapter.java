package com.example.maks.odooprojects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectTask;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<ProjectTask> taskList;
    private final Context context;
    private final LayoutInflater inflater;

    public TaskListAdapter(List<ProjectTask> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(R.layout.task_list_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectTask projectTask = taskList.get(position);

        holder.taskName.setText(projectTask.getName());

        if (projectTask.getDeadline() != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
            holder.taskDeadline.setText(fmt.format(projectTask.getDeadline()));
        }
        if (projectTask.getEmailFrom() != null)
            holder.taskEmailFrom.setText(projectTask.getEmailFrom());

        holder.itemView.setOnClickListener(v -> {
            TaskInfoFragment taskInfoFragment = TaskInfoFragment.newInstance(projectTask.getId());
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, taskInfoFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        TextView taskDeadline;
        TextView taskEmailFrom;
        ImageView taskPriority;
        ImageView taskSchedule;
        ImageView taskAssignedTo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.task_title_tv);
            taskDeadline = itemView.findViewById(R.id.task_deadline_tv);
            taskEmailFrom = itemView.findViewById(R.id.task_customer_email_tv);
            taskPriority = itemView.findViewById(R.id.task_priority_iv);
            taskSchedule = itemView.findViewById(R.id.task_schedule_iv);
            taskAssignedTo = itemView.findViewById(R.id.task_assigned_to_iv);
        }
    }
}
