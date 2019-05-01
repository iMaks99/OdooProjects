package com.example.maks.odooprojects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maks.odooprojects.models.Colors;
import com.example.maks.odooprojects.models.ProjectTask;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

        holder.taskColor.setBackgroundColor(Colors.getColor(projectTask.getColor()));

        holder.showModalSheet.setOnClickListener(v -> {
            View view = inflater.inflate(R.layout.fragment_task_modal_bottom_sheet, null);

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(view);
            dialog.show();
        });

        if(projectTask.isPriority() == 1)
            holder.taskPriority.setImageResource(R.drawable.ic_star_filled);
        else
            holder.taskPriority.setImageResource(R.drawable.ic_star_border);

        holder.itemView.setOnClickListener(v -> {
            TaskInfoFragment taskInfoFragment = TaskInfoFragment.newInstance(projectTask.getId());
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, taskInfoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.taskSchedule.setOnClickListener(v -> {
            FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
            Fragment prev = ((MainActivity) context).getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            DialogFragment dialogFragment = TaskMailActivityDialogFragment.newInstance(projectTask.getId());
            dialogFragment.show(ft, "dialog");
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
        View taskColor;
        ImageView showModalSheet;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.task_title_tv);
            taskDeadline = itemView.findViewById(R.id.task_deadline_tv);
            taskEmailFrom = itemView.findViewById(R.id.task_customer_email_tv);
            taskPriority = itemView.findViewById(R.id.task_priority_iv);
            taskSchedule = itemView.findViewById(R.id.task_schedule_iv);
            taskAssignedTo = itemView.findViewById(R.id.task_assigned_to_iv);
            taskColor = itemView.findViewById(R.id.task_color_v);
            showModalSheet = itemView.findViewById(R.id.show_task_modal_sheet);
        }
    }
}
