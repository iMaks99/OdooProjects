package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskType;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksTabbedFragment extends Fragment
        implements IGetProjectTasks {

    ProgressDialog progressDialog;
    private List<ProjectTask> projectTaskList;
    private List<ProjectTaskType> projectTaskTypes;
    private IGetDataService service;
    private SharedPreferences sharedPreferences;
    int projectId;
    String projectName;

    public TasksTabbedFragment() {
        // Required empty public constructor
    }

    public static TasksTabbedFragment newInstance(int projectId, String projectName) {
        TasksTabbedFragment tasksTabbedFragment = new TasksTabbedFragment();
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
        args.putString("project_name", projectName);
        tasksTabbedFragment.setArguments(args);
        return tasksTabbedFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_tabbed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);

        projectId = getArguments().getInt("project_id");
        projectName = getArguments().getString("project_name");

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


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading tasks...");
        progressDialog.show();

        service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

        sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        Call<List<ProjectTaskType>> stages = service.getProjectTaskStages(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                getArguments().getInt("project_id")
        );

        stages.enqueue(new Callback<List<ProjectTaskType>>() {
            @Override
            public void onResponse(Call<List<ProjectTaskType>> call, Response<List<ProjectTaskType>> response) {
                projectTaskTypes = response.body();
                getTasks(view);
            }

            @Override
            public void onFailure(Call<List<ProjectTaskType>> call, Throwable t) {
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void getTasks(View view) {

        Call<List<ProjectTask>> result = service.getProjectTasks(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                projectId
        );

        result.enqueue(new Callback<List<ProjectTask>>() {
            @Override
            public void onResponse(Call<List<ProjectTask>> call, Response<List<ProjectTask>> response) {

                projectTaskList = response.body();

                final ViewPager viewPager = view.findViewById(R.id.view_pager);
                viewPager.setOffscreenPageLimit(0);

                TabLayout tabLayout = view.findViewById(R.id.tabLayout);
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                TasksTabbedAdapter adapter = new TasksTabbedAdapter(getChildFragmentManager());

                for (ProjectTaskType type : projectTaskTypes) {
                    TasksRecyclerViewFragment tasksRecyclerViewFragment = TasksRecyclerViewFragment
                            .getInstance(type.getId(),
                                    type.getLegendNormal(),
                                    type.getLegendDone(),
                                    type.getLegendBlocked());

                    adapter.addFragment(tasksRecyclerViewFragment, type.getName());
                }

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<ProjectTask>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getTasks(getView());
    }

    @Override
    public List<ProjectTask> returnProjectTask() {
        return projectTaskList;
    }
}
