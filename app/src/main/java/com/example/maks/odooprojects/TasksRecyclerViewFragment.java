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
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;

import java.util.List;

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

    public TasksRecyclerViewFragment() {
        // Required empty public constructor
    }

    private ProgressDialog progressDialog;
    private List<ProjectTask> projectTasks;

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

        if (getParentFragment() instanceof IGetProjectTasks) {
            IGetProjectTasks iGetProjectTasks = (IGetProjectTasks) getParentFragment();
            projectTasks = iGetProjectTasks.returnProjectTask();
            createRecyclerView(view);
        } else {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading tasks...");
            progressDialog.show();

            IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
            Call<List<ProjectTask>> result = service.gettUserTasks(
                    sharedPreferences.getString("token", ""),
                    sharedPreferences.getString("db_name", "")
            );

            result.enqueue(new Callback<List<ProjectTask>>() {
                @Override
                public void onResponse(Call<List<ProjectTask>> call, Response<List<ProjectTask>> response) {
                    projectTasks = response.body();
                    createRecyclerView(view);
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

    private void createRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.task_recycler_view);
        recyclerView.setAdapter(new TaskListAdapter(projectTasks, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
