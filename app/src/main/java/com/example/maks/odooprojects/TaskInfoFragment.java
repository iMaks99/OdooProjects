package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskTag;
import com.example.maks.odooprojects.models.ProjectTaskType;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInfoFragment extends Fragment {

    ProgressDialog progressDialog;
    List<ProjectTaskType> taskTypeList;
    ProjectTask task;

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public static TaskInfoFragment newInstance(int taskId) {
        Bundle args = new Bundle();
        args.putInt("task_id", taskId);
        TaskInfoFragment taskInfoFragment = new TaskInfoFragment();
        taskInfoFragment.setArguments(args);
        return taskInfoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).createBackButton();

        ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        View mCustomView = mInflater.inflate(R.layout.task_info_action_bar, null);
        TextView fragmentTitle = mCustomView.findViewById(R.id.task_info_title);
        ImageView openTaskBottomSheet = mCustomView.findViewById(R.id.open_task_bottom_sheet);

        mActionBar.setCustomView(mCustomView);

        Bundle args = getArguments();
        int taskId = args.getInt("task_id");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading tassk info...");
        progressDialog.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

        Call<ProjectTask> request = service.getTaskById(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                taskId
        );

        ImageView taskProgressColor = view.findViewById(R.id.task_info_progress_iv);
        TextView taskProgressName = view.findViewById(R.id.task_info_progress_name_tv);
        TextView taskName = view.findViewById(R.id.task_info_name_tv);
        TextView taskProject = view.findViewById(R.id.task_info_project_tv);
        TextView taskAssignedTo = view.findViewById(R.id.task_info_assignedto_tv);
        TextView taskDescription = view.findViewById(R.id.task_info_description_tv);
        TextView taskDeadline = view.findViewById(R.id.task_info_deadline_tv);
        ImageView taskPriority = view.findViewById(R.id.task_info_priority_iv);
        ChipGroup taskTags = view.findViewById(R.id.task_info_tags_chg);
        TextView taskCustomerName = view.findViewById(R.id.task_customer_name_tv);
        TextView taskCustomerEmail = view.findViewById(R.id.task_customer_mail_tv);

        request.enqueue(new Callback<ProjectTask>() {
            @Override
            public void onResponse(Call<ProjectTask> call, Response<ProjectTask> response) {
                progressDialog.dismiss();
                task = response.body();

                Call<List<ProjectTaskType>> stages = service.getProjectTaskStages(
                        sharedPreferences.getString("token", ""),
                        sharedPreferences.getString("db_name", ""),
                        task.getProjectId()
                );

                stages.enqueue(new Callback<List<ProjectTaskType>>() {
                    @Override
                    public void onResponse(Call<List<ProjectTaskType>> call, Response<List<ProjectTaskType>> response) {
                        if(response.isSuccessful()){
                            taskTypeList = response.body();
                            ProjectTaskType taskStage = taskTypeList.stream()
                                    .filter(t -> t.getId() == task.getStageId())
                                    .findFirst()
                                    .orElse(null);

                            switch (task.getKanbanState()){
                                case "normal":
                                    taskProgressColor.setImageResource(R.drawable.ic_task_kanban_state_normal);
                                    taskProgressName.setText(taskStage.getLegendNormal());
                                    break;

                                case "done":
                                    taskProgressColor.setImageResource(R.drawable.ic_task_kanban_state_done);
                                    taskProgressName.setText(taskStage.getLegendDone());
                                    break;

                                case "blocked":
                                    taskProgressColor.setImageResource(R.drawable.ic_task_kanban_state_blocked);
                                    taskProgressName.setText(taskStage.getLegendBlocked());
                                    break;
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProjectTaskType>> call, Throwable t) {
                        Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                if (task.getName() != null) {
                    taskName.setText(task.getName());
                    fragmentTitle.setText(task.getName());
                }

                if (task.getProjectName() != null)
                    taskProject.setText(task.getProjectName());

                if (task.getAssignedTo() != null)
                    taskAssignedTo.setText(task.getAssignedTo());

                if (task.getDeadline() != null) {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                    taskDeadline.setText(fmt.format(task.getDeadline()));
                }

                if (task.getDescription() != null)
                    taskDescription.setText(HtmlCompat.fromHtml(task.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                else
                    taskDescription.setVisibility(View.GONE);

                if (task.isPriority() == 1)
                    taskPriority.setImageResource(R.drawable.ic_star_filled);
                else
                    taskPriority.setImageResource(R.drawable.ic_star_border);

                ConstraintLayout constraintLayout = view.findViewById(R.id.task_info_constraint_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);

                float scale = getContext().getResources().getDisplayMetrics().density;
                int pixels = (int) (12 * scale + 0.5f);

                if (task.getTags().size() != 0) {

                    constraintSet.connect(R.id.task_info_description , ConstraintSet.TOP, R.id.task_info_tags_chg ,ConstraintSet.BOTTOM,pixels);
                    for (ProjectTaskTag t : task.getTags()) {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.task_tag_chip, taskTags, false);
                        Chip tag = v.findViewById(R.id.chips_task_tag);
                        tag.setText(t.getName());
                        taskTags.addView(tag);
                    }
                } else {
                    constraintSet.connect(R.id.task_info_description, ConstraintSet.TOP, R.id.task_info_tags, ConstraintSet.BOTTOM,pixels);
                }
                constraintSet.applyTo(constraintLayout);

                if (task.getCustomerDisplayName() != null)
                    taskCustomerName.setText(task.getCustomerDisplayName());

                if (task.getCustomerEmail() != null)
                    taskCustomerEmail.setText(task.getCustomerEmail());

                openTaskBottomSheet.setOnClickListener(v -> {
                    View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_task_info_modal_bottom_sheet, null);
                    BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                    dialog.setContentView(bottomSheetView);

                    TextView changeKanbanState = bottomSheetView.findViewById(R.id.project_task_change_kanban_state_tv);
                    changeKanbanState.setOnClickListener(k -> {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setItems(taskTypeList.stream().map(s -> s.getName()).toArray(String[]::new),
                                (dialogInterface, which) -> {
                                    task.setStageId(taskTypeList.get(which).getId());
                                    editTaskStage(service, sharedPreferences, dialog);
                                });
                        builder.show();
                    });

                    TextView editTask = bottomSheetView.findViewById(R.id.projcet_task_edit_tv);
                    editTask.setOnClickListener(e -> {
                        EditProjectTaskFragment editProjectTaskFragment = EditProjectTaskFragment.newInstance(task.getId());
                        ((MainActivity) getContext()).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, editProjectTaskFragment)
                                .addToBackStack(null)
                                .commit();
                        dialog.dismiss();
                    });

                    TextView deleteTask = bottomSheetView.findViewById(R.id.project_task_delete_tv);
                    deleteTask.setOnClickListener(d -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirmaion")
                                .setMessage("Are you sure you want to delete this record ?")
                                .setPositiveButton("Ok", (deleteDialog, id) -> {

                                    Call<ResponseBody> request = service.deleteProjectTask(
                                            sharedPreferences.getString("token", ""),
                                            sharedPreferences.getString("db_name", ""),
                                            task.getId()
                                    );

                                    request.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            dialog.dismiss();
                                            if (response.isSuccessful()) {
                                                Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Task successfully deleted!", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            } else {
                                                Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Task does not deleted!", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            dialog.dismiss();
                                            Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    });
                                })
                                .setNegativeButton("Cancel", (deleteDialog, id) -> deleteDialog.cancel());
                        builder.show();
                    });

                    dialog.show();
                });

            }

            @Override
            public void onFailure(Call<ProjectTask> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void editTaskStage(IGetDataService service, SharedPreferences sharedPreferences, BottomSheetDialog dialog) {

        Call<ResponseBody> request = service.editProjectTask(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                task
        );

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                } else
                    Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Can't change task stage, please check internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
}
