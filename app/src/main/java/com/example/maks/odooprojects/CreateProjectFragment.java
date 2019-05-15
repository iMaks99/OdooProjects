package com.example.maks.odooprojects;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.models.ResPartner;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProjectFragment extends Fragment {

    List<ResPartner> mEmloyees;
    List<ResPartner> mCustomers;
    ProjectProject mProject;

    public CreateProjectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        EditText projectName = view.findViewById(R.id.change_project_name_ev);
        EditText projectTasksName = view.findViewById(R.id.change_project_tasks_name_ev);

        TextView taskCount = view.findViewById(R.id.project_info_task_count_tv);
        taskCount.setText("0 tasks");

        mProject = new ProjectProject();

        Spinner projectManager = view.findViewById(R.id.change_project_manager_sp);
        ArrayAdapter<String> spinnerAdapterManager = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterManager.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mEmloyees == null)
            getEmployees(spinnerAdapterManager, service, sharedPreferences);
        else
            for (ResPartner partner : mEmloyees)
                spinnerAdapterManager.add(partner.getName());
        projectManager.setAdapter(spinnerAdapterManager);
        projectManager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = projectManager.getItemAtPosition(position).toString();
                mProject.setUserName(selectedUser);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner projectCustomer = view.findViewById(R.id.change_project_customer_sp);
        ArrayAdapter<String> spinnerAdapterCustomer = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mCustomers == null)
            getCustmers(spinnerAdapterCustomer, service, sharedPreferences);
        else
            for (ResPartner partner : mEmloyees) {
                spinnerAdapterCustomer.add("");
                spinnerAdapterCustomer.add(partner.getName());
            }
        projectCustomer.setAdapter(spinnerAdapterCustomer);
        projectCustomer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = projectCustomer.getItemAtPosition(position).toString();
                mProject.setPartner(selectedUser);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RadioButton projectPrivacyPortal = view.findViewById(R.id.project_info_privacy_portal);
        RadioButton projectPrivacyFollowers = view.findViewById(R.id.project_info_privacy_followers);
        RadioButton projectPrivacyEmployees = view.findViewById(R.id.project_info_privacy_employees);

        projectPrivacyPortal.setChecked(false);
        projectPrivacyFollowers.setChecked(true);
        projectPrivacyEmployees.setChecked(false);

        RadioGroup projectPrivacy = view.findViewById(R.id.project_info_privacy_rb);

        String[] privacyValues = view.getResources().getStringArray(R.array.project_privacy);
        mProject.setPrivacyVisibility(privacyValues[0]);

        projectPrivacy.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.project_info_privacy_followers:
                    projectPrivacyPortal.setChecked(false);
                    projectPrivacyFollowers.setChecked(true);
                    projectPrivacyEmployees.setChecked(false);
                    mProject.setPrivacyVisibility(privacyValues[0]);
                    break;

                case R.id.project_info_privacy_employees:
                    projectPrivacyPortal.setChecked(false);
                    projectPrivacyFollowers.setChecked(false);
                    projectPrivacyEmployees.setChecked(true);
                    mProject.setPrivacyVisibility(privacyValues[2]);
                    break;

                case R.id.project_info_privacy_portal:
                    projectPrivacyPortal.setChecked(true);
                    projectPrivacyFollowers.setChecked(false);
                    projectPrivacyEmployees.setChecked(false);
                    mProject.setPrivacyVisibility(privacyValues[1]);
                    break;
            }
        });

        Button projectSave = view.findViewById(R.id.change_project_save_btn);
        projectSave.setOnClickListener(v -> {
            mProject.setName(projectName.getText().toString());
            mProject.setTasksLabel(projectTasksName.getText().toString());

            if (mProject.getName() == null || mProject.getName().equals(""))
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Please fill project name", Snackbar.LENGTH_LONG)
                        .setAction("Add project name", view12 -> {

                            projectName.requestFocus();
                            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imgr.showSoftInput(projectName, InputMethodManager.SHOW_IMPLICIT);

                        }).show();
            else
                createNewProject(service, sharedPreferences, mProject);
        });

        Button projectDiscard = view.findViewById(R.id.change_project_discard_btn);
        projectDiscard.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your changes will be discarded. Do you want to proceed?")
                    .setPositiveButton("Ok", (dialog, id) -> getActivity().getFragmentManager().popBackStack())
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            builder.show();
        });
    }

    private void createNewProject(IGetDataService service, SharedPreferences sharedPreferences, ProjectProject mProject) {
        Call<Integer> request = service.newProject(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                mProject
        );

        request.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Task successfully created!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    mProject.setId(response.body());

                   /* TaskInfoFragment taskInfoFragment = TaskInfoFragment.newInstance(mProject.getId());
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStackImmediate();
                    fm.beginTransaction()
                            .replace(R.id.content_frame, taskInfoFragment)
                            .addToBackStack(null)
                            .commit();*/

                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Project does not created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }

    private void getCustmers(ArrayAdapter<String> spinnerAdapterCustomer, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ResPartner>> request = service.getPartnersAll(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ResPartner>>() {
            @Override
            public void onResponse(Call<List<ResPartner>> call, Response<List<ResPartner>> response) {

                if (response.isSuccessful()) {
                    mCustomers = response.body();

                    for (ResPartner partner : mCustomers)
                        if (partner.getDisplayedName() == null)
                            spinnerAdapterCustomer.add(partner.getName());
                        else
                            spinnerAdapterCustomer.add(partner.getDisplayedName());
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResPartner>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getEmployees(ArrayAdapter<String> spinnerAdapterManager, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ResPartner>> request = service.getUserPartners(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ResPartner>>() {
            @Override
            public void onResponse(Call<List<ResPartner>> call, Response<List<ResPartner>> response) {
                if (response.isSuccessful()) {
                    mEmloyees = response.body();

                    for (ResPartner partner : mEmloyees)
                        spinnerAdapterManager.add(partner.getName());
                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<List<ResPartner>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
