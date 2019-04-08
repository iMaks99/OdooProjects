package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.network.GetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectsRecyclerViewFragment extends Fragment {

    ProgressDialog progressDialog;


    public ProjectsRecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Projects");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading projects...");
        progressDialog.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<ProjectProject>> result = service.getAllProjects(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        result.enqueue(new Callback<List<ProjectProject>>() {
            @Override
            public void onResponse(Call<List<ProjectProject>> call, Response<List<ProjectProject>> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<ProjectProject>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<ProjectProject> body) {
        RecyclerView recyclerView = getActivity().findViewById(R.id.project_recycler_view);
        recyclerView.setAdapter(new ProjectListAdapter(getContext(), body));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
