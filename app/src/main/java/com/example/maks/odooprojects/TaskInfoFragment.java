package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskTag;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInfoFragment extends Fragment {

    ProgressDialog progressDialog;

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public static TaskInfoFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("task_id", id);
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
        openTaskBottomSheet.setOnClickListener(v -> {
            Snackbar.make(getActivity().findViewById(R.id.content_frame), "Bottom sheet opened!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });

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
                ProjectTask task = response.body();


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

                if (task.isPriority() == 1)
                    taskPriority.setImageResource(R.drawable.ic_star_filled);
                else
                    taskPriority.setImageResource(R.drawable.ic_star_border);

                if (task.getTags() != null) {
                    for (ProjectTaskTag t : task.getTags()) {
                        View v = LayoutInflater.from(getContext()).inflate(R.layout.task_tag_chip, taskTags, false);
                        Chip tag = v.findViewById(R.id.chips_task_tag);
                        tag.setText(t.getName());
                        taskTags.addView(tag);
                    }
                }

                if (task.getCustomerDisplayName() != null)
                    taskCustomerName.setText(task.getCustomerDisplayName());

                if (task.getCustomerEmail() != null)
                    taskCustomerEmail.setText(task.getCustomerEmail());
            }

            @Override
            public void onFailure(Call<ProjectTask> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
