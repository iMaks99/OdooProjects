package com.example.maks.odooprojects;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksRecyclerViewFragment extends Fragment {

    private ProgressDialog progressDialog;
    private List<ProjectTask> projectTasks;

    public TasksRecyclerViewFragment() {
        // Required empty public constructor
    }

    public static TasksRecyclerViewFragment newInstance(int stageId, int projectId, String projectName) {
        TasksRecyclerViewFragment tasksRecyclerViewFragment = new TasksRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt("stage_id", stageId);
        args.putInt("project_id", projectId);
        args.putString("project_name", projectName);
        tasksRecyclerViewFragment.setArguments(args);
        return tasksRecyclerViewFragment;
    }

    public static TasksRecyclerViewFragment getInstance(int stageId) {
        TasksRecyclerViewFragment tasksRecyclerViewFragment = new TasksRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt("stage_id", stageId);
        tasksRecyclerViewFragment.setArguments(args);
        return tasksRecyclerViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Tasks");

        Bundle args = getArguments();
        int stageId = args.getInt("stage_id");
        int projectId = args.getInt("project_id");
        String projectName = args.getString("project_name");

        FloatingActionButton fab = view.findViewById(R.id.add_project_task_fab);
        fab.setOnClickListener(v -> {

            CreateProjectTaskFragment createProjectTaskFragment = CreateProjectTaskFragment
                    .newInstance(projectId, projectName);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, createProjectTaskFragment)
                    .addToBackStack(null)
                    .commit();
        });

        if (getParentFragment() instanceof IGetProjectTasks) {
            IGetProjectTasks iGetProjectTasks = (IGetProjectTasks) getParentFragment();
            projectTasks = iGetProjectTasks.returnProjectTask();
            createRecyclerView(view, stageId);
        } else {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading tasks...");
            progressDialog.show();

            IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
            Call<List<ProjectTask>> result = service.getUserTasks(
                    sharedPreferences.getString("token", ""),
                    sharedPreferences.getString("db_name", "")
            );

            result.enqueue(new Callback<List<ProjectTask>>() {
                @Override
                public void onResponse(Call<List<ProjectTask>> call, Response<List<ProjectTask>> response) {
                    projectTasks = response.body();
                    createRecyclerView(view, stageId);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<List<ProjectTask>> call, Throwable t) {
                    Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void createRecyclerView(View view, int stageId) {
        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);

        List<ProjectTask> projectTasksByStage = projectTasks.stream()
                .filter(t -> t.getStageId() == stageId)
                .collect(Collectors.toList());
        recyclerView.setAdapter(new TaskListAdapter(projectTasksByStage, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
