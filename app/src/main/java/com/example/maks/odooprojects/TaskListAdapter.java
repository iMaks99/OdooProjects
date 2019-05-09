package com.example.maks.odooprojects;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maks.odooprojects.models.Colors;
import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskType;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private List<ProjectTask> taskList;
    private List<ProjectTaskType> taskTypeList;
    private int stageId;
    private final Context context;
    private final LayoutInflater inflater;
    private int i;
    SharedPreferences sharedPreferences;
    IGetDataService service;


    public TaskListAdapter(List<ProjectTask> taskList, int stageId, List<ProjectTaskType> taskTypesList, Context context) {
        this.taskList = taskList;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.taskTypeList = taskTypesList;
        this.stageId = stageId;
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

        sharedPreferences = context.getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

        Call<ResponseBody> editRequest = service.editProjectTask(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                taskList.get(position)
        );

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

            TextView changeKanbanState = view.findViewById(R.id.project_task_change_kanban_state_tv);
            changeKanbanState.setOnClickListener(k -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(taskTypeList.stream().map(s -> s.getName()).toArray(String[]::new),
                        (dialogInterface, which) -> {
                            taskList.get(position).setStageId(taskTypeList.get(which).getId());
                            editTask(editRequest);

                            ViewPager viewPager = ((MainActivity) context).findViewById(R.id.view_pager);
                            Fragment taskChangedFragment = ((TasksTabbedAdapter) viewPager.getAdapter()).getFragmentByTitle(taskTypeList.get(which).getName());

                            if(taskChangedFragment instanceof TasksRecyclerViewFragment) {
                                ((TasksRecyclerViewFragment) taskChangedFragment).adapter.taskList.add(taskList.get(position));
                                ((TasksRecyclerViewFragment) taskChangedFragment).adapter.notifyDataSetChanged();
                            }

                            taskList.remove(position);
                            notifyItemRemoved(position);
                        });
                builder.show();
            });

            LinearLayout taskColors = view.findViewById(R.id.task_colors_ll);
            Map<View, Integer> colorViewIdMap = new HashMap<>();
            for (i = 0; i < Colors.getColors().length; ++i) {
                View colorView = new View(context);
                colorView.setBackgroundColor(Colors.getColor(i));
                float scale = context.getResources().getDisplayMetrics().density;
                int pixels = (int) (50 * scale + 0.5f);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(pixels,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                viewParams.setMarginEnd(4);
                colorView.setLayoutParams(viewParams);
                colorViewIdMap.put(colorView, i);
                colorView.setOnClickListener(c -> {
                    taskList.get(position).setColor(colorViewIdMap.get(colorView));
                    editTask(editRequest, position, dialog);
                });


                taskColors.addView(colorView);
            }

            TextView editTask = view.findViewById(R.id.projcet_task_edit_tv);
            editTask.setOnClickListener(e -> {
                EditProjectTaskFragment editProjectTaskFragment = EditProjectTaskFragment.newInstance(projectTask.getId());
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, editProjectTaskFragment)
                        .addToBackStack(null)
                        .commit();
                dialog.dismiss();
            });

            TextView deleteTask = view.findViewById(R.id.project_task_delete_tv);
            deleteTask.setOnClickListener(d -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmaion")
                        .setMessage("Are you sure you want to delete this record ?")
                        .setPositiveButton("Ok", (deleteDialog, id) -> {

                            Call<ResponseBody> request = service.deleteProjectTask(
                                    sharedPreferences.getString("token", ""),
                                    sharedPreferences.getString("db_name", ""),
                                    projectTask.getId()
                            );

                            request.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    dialog.dismiss();
                                    if (response.isSuccessful()) {
                                        Snackbar.make(((MainActivity) context).getCurrentFocus(), "Task successfully deleted!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        taskList.remove(position);
                                        notifyItemRemoved(position);
                                    } else {
                                        Snackbar.make(((MainActivity) context).getCurrentFocus(), "Task does not deleted!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    dialog.dismiss();
                                    Snackbar.make(((MainActivity) context).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", (deleteDialog, id) -> deleteDialog.cancel());
                builder.show();
            });

            dialog.show();
        });

        if (projectTask.isPriority() == 1)
            holder.taskPriority.setImageResource(R.drawable.ic_star_filled);
        else
            holder.taskPriority.setImageResource(R.drawable.ic_star_border);

        holder.taskPriority.setOnClickListener(v -> {
            if (projectTask.isPriority() == 0)
                taskList.get(position).setIsPriority(1);
            else
                taskList.get(position).setIsPriority(0);

            editTask(editRequest);
            notifyItemRangeChanged(position, taskList.size());
        });

        switch (projectTask.getKanbanState()) {
            case "normal":
                holder.taskProgress.setImageResource(R.drawable.ic_task_kanban_state_normal);
                break;

            case "done":
                holder.taskProgress.setImageResource(R.drawable.ic_task_kanban_state_done);
                break;

            case "blocked":
                holder.taskProgress.setImageResource(R.drawable.ic_task_kanban_state_blocked);
                break;
        }
        holder.taskProgress.setOnClickListener(v -> {

            String[] kanbanDialog = new String[2];
            Map<String, String> kanban = new HashMap<>();

            switch (projectTask.getKanbanState()) {
                case "normal":
                    kanbanDialog[0] = taskTypeList.get(stageId).getLegendDone();
                    kanbanDialog[1] = taskTypeList.get(stageId).getLegendBlocked();
                    kanban.put(kanbanDialog[0], "done");
                    kanban.put(kanbanDialog[1], "blocked");
                    break;

                case "done":
                    kanbanDialog[0] = taskTypeList.get(stageId).getLegendNormal();
                    kanbanDialog[1] = taskTypeList.get(stageId).getLegendBlocked();
                    kanban.put(kanbanDialog[0], "normal");
                    kanban.put(kanbanDialog[1], "blocked");
                    break;

                case "blocked":
                    kanbanDialog[0] = taskTypeList.get(stageId).getLegendDone();
                    kanbanDialog[1] = taskTypeList.get(stageId).getLegendNormal();
                    kanban.put(kanbanDialog[0], "done");
                    kanban.put(kanbanDialog[1], "normal");
                    break;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(kanbanDialog,
                    (dialogInterface, which) -> {
                        projectTask.setKanbanState(kanban.get(kanbanDialog[which]));
                        editTask(editRequest);
                        notifyItemRangeChanged(position, taskList.size());
                    });
            builder.show();
        });

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
        ImageView taskProgress;
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
            taskProgress = itemView.findViewById(R.id.task_progress_iv);
        }
    }

    void editTask(Call<ResponseBody> request, int position, BottomSheetDialog dialog) {

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    notifyItemRangeChanged(position, taskList.size());
                    dialog.dismiss();
                } else {
                    Snackbar.make(((MainActivity) context).getCurrentFocus(), "Can't change task color, please check internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(((MainActivity) context).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    void editTask(Call<ResponseBody> request) {
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful())
                    Snackbar.make(((MainActivity) context).getCurrentFocus(), "Can't change task priority, please check internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(((MainActivity) context).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
}
