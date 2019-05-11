package com.example.maks.odooprojects;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maks.odooprojects.models.HRDepartment;
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
public class DepartmentsRecyclerViewFragment extends Fragment {

    ProgressDialog progressDialog;

    public DepartmentsRecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_departments_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).setToolbarTitleEnabled("Departments");
        ((MainActivity) getActivity()).crateMenuButton();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading departments...");
        progressDialog.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        Call<List<HRDepartment>> result = service.getAllDepartments(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        result.enqueue(new Callback<List<HRDepartment>>() {
            @Override
            public void onResponse(Call<List<HRDepartment>> call, Response<List<HRDepartment>> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<HRDepartment>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<HRDepartment> body) {
        RecyclerView recyclerView = getView().findViewById(R.id.department_recycler_view);
        recyclerView.setAdapter(new DepartmentListAdapter(getContext(), body));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
