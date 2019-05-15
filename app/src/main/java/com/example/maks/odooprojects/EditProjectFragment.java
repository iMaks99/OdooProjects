package com.example.maks.odooprojects;


import android.annotation.SuppressLint;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProjectFragment extends Fragment {

    List<ResPartner> mEmloyees;
    List<ResPartner> mCustomers;
    ProjectProject mProject;
    Spinner projectCustomer;
    Spinner projectManager;

    public EditProjectFragment() {
        // Required empty public constructor
    }

    public static EditProjectFragment newInstance(int projectId) {
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
        EditProjectFragment editProjectFragment = new EditProjectFragment();
        editProjectFragment.setArguments(args);
        return editProjectFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_project, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        int projectId = args.getInt("project_id");

        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        Call<ProjectProject> request = service.getProjectById(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                projectId
        );

        request.enqueue(new Callback<ProjectProject>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onResponse(Call<ProjectProject> call, Response<ProjectProject> response) {
                if (response.isSuccessful()) {
                    mProject = response.body();

                    EditText projectName = view.findViewById(R.id.change_project_name_ev);
                    projectName.setText(mProject.getName());
                    EditText projectTasksName = view.findViewById(R.id.change_project_tasks_name_ev);
                    projectTasksName.setText(mProject.getTasksLabel());

                    TextView taskCount = view.findViewById(R.id.change_project_tasks_tv);
                    taskCount.setText(String.format("%d tasks", mProject.getTasksCount()));

                    projectManager = view.findViewById(R.id.change_project_manager_sp);
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

                    projectCustomer = view.findViewById(R.id.change_project_customer_sp);
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

                    RadioButton projectPrivacyPortal = view.findViewById(R.id.change_project_privacy_following_customers);
                    RadioButton projectPrivacyFollowers = view.findViewById(R.id.change_project_privacy_invitation_only);
                    RadioButton projectPrivacyEmployees = view.findViewById(R.id.change_project_privacy_all_employees);

                    String[] privacyValues = view.getResources().getStringArray(R.array.project_privacy);

                    switch (mProject.getPrivacyVisibility()) {
                        case "followers":
                            projectPrivacyPortal.setChecked(false);
                            projectPrivacyFollowers.setChecked(true);
                            projectPrivacyEmployees.setChecked(false);
                            break;

                        case "employees":
                            projectPrivacyPortal.setChecked(false);
                            projectPrivacyFollowers.setChecked(false);
                            projectPrivacyEmployees.setChecked(true);
                            break;

                        case "portal":
                            projectPrivacyPortal.setChecked(true);
                            projectPrivacyFollowers.setChecked(false);
                            projectPrivacyEmployees.setChecked(false);
                            break;

                    }

                    RadioGroup projectPrivacy = view.findViewById(R.id.change_project_privacy_rg);
                    projectPrivacy.setOnCheckedChangeListener((group, checkedId) -> {
                        switch (checkedId) {
                            case R.id.change_project_privacy_invitation_only:
                                projectPrivacyPortal.setChecked(false);
                                projectPrivacyFollowers.setChecked(true);
                                projectPrivacyEmployees.setChecked(false);
                                mProject.setPrivacyVisibility(privacyValues[0]);
                                break;

                            case R.id.change_project_privacy_all_employees:
                                projectPrivacyPortal.setChecked(false);
                                projectPrivacyFollowers.setChecked(false);
                                projectPrivacyEmployees.setChecked(true);
                                mProject.setPrivacyVisibility(privacyValues[2]);
                                break;

                            case R.id.change_project_privacy_following_customers:
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
                            editProject(service, sharedPreferences);
                    });

                    Button projectDiscard = view.findViewById(R.id.change_project_discard_btn);
                    projectDiscard.setOnClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Your changes will be discarded. Do you want to proceed?")
                                .setPositiveButton("Ok", (dialog, id) -> getActivity().getFragmentManager().popBackStack())
                                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                        builder.show();
                    });
                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<ProjectProject> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void editProject(IGetDataService service, SharedPreferences sharedPreferences) {
        Call<ResponseBody> request = service.editProject(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                mProject
        );

        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Project successfully edited!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getFragmentManager().popBackStack();
                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Project does not edited", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

                    projectCustomer.setSelection(spinnerAdapterCustomer.getPosition(mProject.getPartner()));

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

                    projectManager.setSelection(spinnerAdapterManager.getPosition(mProject.getUserName()));

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
