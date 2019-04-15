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
import com.example.maks.odooprojects.models.HREmployee;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;

import java.util.List;

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
public class EmployeesRecyclerViewFragment extends Fragment {

    private ProgressDialog progressDialog;

    public EmployeesRecyclerViewFragment() {
        // Required empty public constructor
    }

    public static EmployeesRecyclerViewFragment newInstance(int id) {
        EmployeesRecyclerViewFragment employeesRecyclerViewFragment = new EmployeesRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt("department_id", id);
        employeesRecyclerViewFragment.setArguments(args);
        return employeesRecyclerViewFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employees_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        int departmentId = args.getInt("department_id");

        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading employees of department...");
        progressDialog.show();

        Call<List<HREmployee>> request = service.getDepartmentEmployees(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                departmentId
        );

        request.enqueue(new Callback<List<HREmployee>>() {
            @Override
            public void onResponse(Call<List<HREmployee>> call, Response<List<HREmployee>> response) {
                progressDialog.dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<HREmployee>> call, Throwable t) {
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void generateDataList(List<HREmployee> body) {
        RecyclerView recyclerView = getView().findViewById(R.id.employee_recycler_view);
        recyclerView.setAdapter(new EmployeeListAdapter(getContext(), body));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
