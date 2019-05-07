package com.example.maks.odooprojects;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maks.odooprojects.models.Colors;
import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                            SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
                            IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

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
