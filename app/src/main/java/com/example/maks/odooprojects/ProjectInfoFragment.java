package com.example.maks.odooprojects;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectInfoFragment extends Fragment {

    private ProjectProject mProject;

    public ProjectInfoFragment() {
        // Required empty public constructor
    }

    public static ProjectInfoFragment newInstance(int projectId) {
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
        ProjectInfoFragment projectInfoFragment = new ProjectInfoFragment();
        projectInfoFragment.setArguments(args);
        return projectInfoFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).createBackButton();

        ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        TextView fragmentTitle = mCustomView.findViewById(R.id.action_bar_title);
        ImageView openBottomSheet = mCustomView.findViewById(R.id.action_bar_open_bottom_sheet);

        mActionBar.setCustomView(mCustomView);

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
            @Override
            public void onResponse(Call<ProjectProject> call, Response<ProjectProject> response) {
                if (response.isSuccessful()) {
                    mProject = response.body();

                    fragmentTitle.setText(mProject.getName());
                    openBottomSheet.setOnClickListener(v -> {
                        View bottomSheetView = getLayoutInflater().inflate(R.layout.fragment_project_info_modal_bottom_sheet, null);
                        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                        dialog.setContentView(bottomSheetView);

                        TextView editProject = bottomSheetView.findViewById(R.id.project_info_bottom_edit_tv);
                        editProject.setOnClickListener(e -> {
                            EditProjectFragment editProjectFragment = EditProjectFragment.newInstance(mProject.getId());
                            ((MainActivity) getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, editProjectFragment)
                                    .addToBackStack(null)
                                    .commit();
                            dialog.dismiss();
                        });

                        TextView deleteProject = bottomSheetView.findViewById(R.id.project_info_bottom_delete_tv);
                        deleteProject.setOnClickListener(d -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Confirmaion")
                                    .setMessage("Are you sure you want to delete this record ?")
                                    .setPositiveButton("Ok", (deleteDialog, id) -> {

                                        Call<ResponseBody> request = service.deleteProject(
                                                sharedPreferences.getString("token", ""),
                                                sharedPreferences.getString("db_name", ""),
                                                projectId
                                        );

                                        request.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                dialog.dismiss();
                                                if (response.isSuccessful()) {
                                                    Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Project successfully deleted!", Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                } else {
                                                    Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Project does not deleted!", Snackbar.LENGTH_LONG)
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

                    TextView projectName = view.findViewById(R.id.project_info_name_tv);
                    projectName.setText(mProject.getName());
                    TextView projectTaskLabel = view.findViewById(R.id.project_info_tasks_label_tv);
                    projectTaskLabel.setText(mProject.getTasksLabel());

                    TextView projectTaskCount = view.findViewById(R.id.project_info_task_count_tv);
                    if (mProject.getTasksCount() > 1)
                        projectTaskCount.setText(String.format("%d tasks", mProject.getTasksCount()));
                    else
                        projectTaskCount.setText(String.format("%d task", mProject.getTasksCount()));

                    TextView projectManager = view.findViewById(R.id.project_info_manager_tv);
                    projectManager.setText(mProject.getUserName());

                    TextView projectCustomer = view.findViewById(R.id.project_info_customer);
                    projectCustomer.setText(mProject.getPartner());

                    RadioButton privacyPortal = view.findViewById(R.id.project_info_privacy_portal);
                    RadioButton privacyEmployee = view.findViewById(R.id.project_info_privacy_employees);
                    RadioButton privacyFollowers = view.findViewById(R.id.project_info_privacy_followers);

                    privacyEmployee.setClickable(false);
                    privacyFollowers.setClickable(false);
                    privacyPortal.setClickable(false);

                    switch (mProject.getPrivacyVisibility()) {
                        case "followers":
                            privacyPortal.setChecked(false);
                            privacyFollowers.setChecked(true);
                            privacyEmployee.setChecked(false);
                            break;

                        case "employees":
                            privacyPortal.setChecked(false);
                            privacyFollowers.setChecked(false);
                            privacyEmployee.setChecked(true);
                            break;

                        case "portal":
                            privacyPortal.setChecked(true);
                            privacyFollowers.setChecked(false);
                            privacyEmployee.setChecked(false);
                            break;
                    }

                    LinearLayout tasksLL = view.findViewById(R.id.project_tasks_count_ll);
                    tasksLL.setOnClickListener(v -> {
                        TasksTabbedFragment tasksTabbedFragment = TasksTabbedFragment
                                .newInstance(mProject.getId(), mProject.getName(), mProject.getTasksLabel());
                        ((MainActivity) getContext()).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, tasksTabbedFragment, "projectTaskFragment")
                                .addToBackStack(null)
                                .commit();
                    });
                } else
                    Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Please check internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<ProjectProject> call, Throwable t) {
                Snackbar.make(((MainActivity) getContext()).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
