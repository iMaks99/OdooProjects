package com.example.maks.odooprojects;


import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.models.ProjectTask;
import com.example.maks.odooprojects.models.ProjectTaskTag;
import com.example.maks.odooprojects.models.ProjectTaskType;
import com.example.maks.odooprojects.models.ResPartner;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateProjectTaskFragment extends Fragment {

    List<ResPartner> mPartners;
    List<ResPartner> mPartnersAll;
    List<ProjectProject> mProjects;
    List<ProjectTaskType> mStages;
    List<ProjectTaskTag> mTags;
    Spinner taskCustomers;
    ProjectTaskType taskStage;
    EditText taskCustomerEmail;
    ProjectTask mTask;
    ImageView taskProgress;
    TextView taskProgressLegend;
    Spinner taskStages;

    public CreateProjectTaskFragment() {
        // Required empty public constructor
    }

    public static CreateProjectTaskFragment newInstance(int projectId, String projectName) {
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
        args.putString("project_name", projectName);

        CreateProjectTaskFragment createProjectTaskFragment = new CreateProjectTaskFragment();
        createProjectTaskFragment.setArguments(args);
        return createProjectTaskFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_project_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).createBackButton();
        ((MainActivity) getActivity()).setToolbarTitleEnabled("Creating task");

        Bundle args = getArguments();
        int projectId = args.getInt("project_id");
        String projectName = args.getString("project_name");

        mTask = new ProjectTask();
        mTask.setProjectName(projectName);
        mTask.setIsPriority(0);

        TextView taskName = view.findViewById(R.id.new_project_task_name_ev);
        ImageView taskPriority = view.findViewById(R.id.new_project_task_priority_iv);
        taskPriority.setOnClickListener(v -> {
            if (mTask.isPriority() == 0) {
                mTask.setIsPriority(1);
                taskPriority.setImageResource(R.drawable.ic_star_filled);
            } else {
                mTask.setIsPriority(0);
                taskPriority.setImageResource(R.drawable.ic_star_border);
            }
        });

        taskProgress = view.findViewById(R.id.new_project_task_progress_iv);
        taskProgressLegend = view.findViewById(R.id.new_project_task_progress_name_tv);

        EditText taskDeadline = view.findViewById(R.id.new_project_task_deadline_ev);
        final Calendar deadlineCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker = (view1, year, month, dayOfMonth) -> {
            deadlineCalendar.set(Calendar.YEAR, year);
            deadlineCalendar.set(Calendar.MONTH, month);
            deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dateFormat = "dd.MM.yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ROOT);
            taskDeadline.setText(simpleDateFormat.format(deadlineCalendar.getTime()));

            mTask.setDeadline(deadlineCalendar.getTime());
        };

        taskDeadline.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), datePicker,
                    deadlineCalendar.get(Calendar.YEAR),
                    deadlineCalendar.get(Calendar.MONTH),
                    deadlineCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        taskStages = view.findViewById(R.id.new_project_task_stage_sp);
        ArrayAdapter<String> spinnerAdapterStages = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskStages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStage = taskStages.getItemAtPosition(position).toString();
                mTask.setStageId(mStages.stream()
                        .filter(s -> s.getName().equals(selectedStage))
                        .findFirst()
                        .orElse(null)
                        .getId());

                mTask.setKanbanState("normal");
                onTaskStageChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner taskProject = view.findViewById(R.id.new_project_task_project_sp);
        ArrayAdapter<String> spinnerAdapterProjects = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterProjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mProjects == null)
            getProjects(spinnerAdapterProjects, service, sharedPreferences);
        else
            for (ProjectProject project : mProjects)
                spinnerAdapterProjects.add(project.getName());
        taskProject.setAdapter(spinnerAdapterProjects);
        taskProject.setSelection(spinnerAdapterProjects.getPosition(projectName));
        taskProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerAdapterStages.clear();
                String selectedProject = taskProject.getItemAtPosition(position).toString();
                int selectedProjectId = mProjects.stream()
                        .filter(p -> p.getName().equals(selectedProject))
                        .findFirst()
                        .orElse(null)
                        .getId();
                getStages(spinnerAdapterStages, service, sharedPreferences, selectedProjectId);
                mTask.setProjectName(selectedProject);
                taskStages.setAdapter(spinnerAdapterStages);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner taskAssigned = view.findViewById(R.id.new_project_task_assigned_sp);
        ArrayAdapter<String> spinnerAdapterPartner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterPartner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mPartners == null)
            getPartners(spinnerAdapterPartner, service, sharedPreferences);
        else
            for (ResPartner partner : mPartners)
                spinnerAdapterPartner.add(partner.getName());
        taskAssigned.setAdapter(spinnerAdapterPartner);
        taskAssigned.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTask.setAssignedTo(taskAssigned.getItemAtPosition(position).toString());

                mTask.setAssignedToId(mPartners.stream()
                        .filter(p -> p.getName().equals(mTask.getAssignedTo()))
                        .findFirst()
                        .orElse(null)
                        .getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Call<List<ProjectTaskTag>> request = service.getTaskTagsAll(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ProjectTaskTag>>() {
            @Override
            public void onResponse(Call<List<ProjectTaskTag>> call, Response<List<ProjectTaskTag>> response) {
                mTags = response.body();
                ChipGroup selectTaskTags = view.findViewById(R.id.new_project_task_tags_chg);
                for (ProjectTaskTag tag : mTags) {
                    View v = LayoutInflater.from(getContext()).inflate(R.layout.task_tag_select_chip, selectTaskTags, false);
                    Chip tagChip = v.findViewById(R.id.filter_chip_task_tag);
                    tagChip.setText(tag.getName());

                    tagChip.setOnCheckedChangeListener((ch, isSelecred) -> {

                        ProjectTaskTag projectTaskTag = mTags.stream().filter(t -> t.getName() == ch.getText().toString()).findFirst().orElse(null);

                        if (mTask.getTags() == null)
                            mTask.setTags(new ArrayList<>());

                        if (isSelecred)
                            mTask.getTags().add(projectTaskTag);
                        else
                            mTask.getTags().remove(projectTaskTag);
                    });

                    selectTaskTags.addView(tagChip);
                }
            }

            @Override
            public void onFailure(Call<List<ProjectTaskTag>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        EditText taskDescription = view.findViewById(R.id.new_project_task_description_ev);
        taskCustomerEmail = view.findViewById(R.id.new_project_task_customer_email_ev);

        taskCustomers = view.findViewById(R.id.new_project_task_customer_name_sp);
        ArrayAdapter<String> spinnerAdapterCustomer = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getAllPartners(spinnerAdapterCustomer, service, sharedPreferences);

        Button taskSave = view.findViewById(R.id.new_project_task_save_btn);
        taskSave.setOnClickListener(v -> {
            mTask.setName(taskName.getText().toString());
            mTask.setDescription(HtmlCompat.toHtml(taskDescription.getText(), HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));

            if (mTask.getName() == null || mTask.getName().equals(""))
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Please fill task name", Snackbar.LENGTH_LONG)
                        .setAction("Add task name", view12 -> {

                            taskName.requestFocus();
                            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imgr.showSoftInput(taskName, InputMethodManager.SHOW_IMPLICIT);

                        }).show();
            else
                createNewTask(service, sharedPreferences, mTask);
        });

        Button taskDiscard = view.findViewById(R.id.new_project_task_discard_btn);
        taskDiscard.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your changes will be discarded. Do you want to proceed?")
                    .setPositiveButton("Ok", (dialog, id) -> getActivity().getFragmentManager().popBackStack())
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            builder.show();
        });
    }

    private void onTaskStageChanged() {
        switch (mTask.getKanbanState()) {
            case "normal":
                taskProgress.setImageResource(R.drawable.ic_task_kanban_state_normal);
                taskProgressLegend.setText(taskStage.getLegendNormal());
                break;

            case "done":
                taskProgress.setImageResource(R.drawable.ic_task_kanban_state_done);
                taskProgressLegend.setText(taskStage.getLegendDone());
                break;

            case "blocked":
                taskProgress.setImageResource(R.drawable.ic_task_kanban_state_blocked);
                taskProgressLegend.setText(taskStage.getLegendBlocked());
                break;
        }
    }

    private void getStages(ArrayAdapter<String> spinnerAdapterStages, IGetDataService service,
                           SharedPreferences sharedPreferences, int projectId) {
        Call<List<ProjectTaskType>> request = service.getProjectTaskStages(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                projectId
        );

        request.enqueue(new Callback<List<ProjectTaskType>>() {
            @Override
            public void onResponse(Call<List<ProjectTaskType>> call, Response<List<ProjectTaskType>> response) {
                mStages = response.body();

                for (ProjectTaskType taskType : mStages)
                    spinnerAdapterStages.add(taskType.getName());

                mTask.setStageId(mStages.get(0).getId());

                taskStage = mStages.stream()
                        .filter(s -> s.getId() == mTask.getStageId())
                        .findFirst()
                        .orElse(null);

                mTask.setKanbanState("normal");
                onTaskStageChanged();

                taskProgress.setOnClickListener(v -> {
                    String[] kanbanDialog = new String[2];
                    Map<String, String> kanban = new HashMap<>();

                    switch (mTask.getKanbanState()) {
                        case "normal":
                            kanbanDialog[0] = taskStage.getLegendDone();
                            kanbanDialog[1] = taskStage.getLegendBlocked();
                            kanban.put(kanbanDialog[0], "done");
                            kanban.put(kanbanDialog[1], "blocked");
                            break;

                        case "done":
                            kanbanDialog[0] = taskStage.getLegendNormal();
                            kanbanDialog[1] = taskStage.getLegendBlocked();
                            kanban.put(kanbanDialog[0], "normal");
                            kanban.put(kanbanDialog[1], "blocked");
                            break;

                        case "blocked":
                            kanbanDialog[0] = taskStage.getLegendDone();
                            kanbanDialog[1] = taskStage.getLegendNormal();
                            kanban.put(kanbanDialog[0], "done");
                            kanban.put(kanbanDialog[1], "normal");
                            break;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setItems(kanbanDialog,
                            (dialogInterface, which) -> {
                                mTask.setKanbanState(kanban.get(kanbanDialog[which]));
                                onTaskStageChanged();
                            });
                    builder.show();
                });
            }

            @Override
            public void onFailure(Call<List<ProjectTaskType>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getProjects(ArrayAdapter<String> arrayAdapter, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ProjectProject>> request = service.getAllProjects(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ProjectProject>>() {
            @Override
            public void onResponse(Call<List<ProjectProject>> call, Response<List<ProjectProject>> response) {
                mProjects = response.body();

                for (ProjectProject project : mProjects)
                    arrayAdapter.add(project.getName());
            }

            @Override
            public void onFailure(Call<List<ProjectProject>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getPartners(ArrayAdapter<String> stringArrayAdapter, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ResPartner>> request = service.getUserPartners(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ResPartner>>() {
            @Override
            public void onResponse(Call<List<ResPartner>> call, Response<List<ResPartner>> response) {
                mPartners = response.body();

                for (ResPartner partner : mPartners)
                    stringArrayAdapter.add(partner.getName());
            }

            @Override
            public void onFailure(Call<List<ResPartner>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getAllPartners(ArrayAdapter<String> stringArrayAdapter, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ResPartner>> request = service.getPartnersAll(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ResPartner>>() {
            @Override
            public void onResponse(Call<List<ResPartner>> call, Response<List<ResPartner>> response) {

                if (response.isSuccessful()) {
                    mPartnersAll = response.body();

                    for (ResPartner partner : mPartnersAll)
                        if (partner.getDisplayedName() == null)
                            stringArrayAdapter.add(partner.getName());
                        else
                            stringArrayAdapter.add(partner.getDisplayedName());


                    taskCustomers.setAdapter(stringArrayAdapter);
                    taskCustomers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ResPartner selectedCustomer = mPartnersAll.stream()
                                    .filter(c -> {
                                        if (c.getDisplayedName() != null)
                                            return c.getDisplayedName().equals(taskCustomers.getItemAtPosition(position).toString());
                                        return c.getName().equals(taskCustomers.getItemAtPosition(position).toString());
                                    })
                                    .findFirst()
                                    .orElse(null);

                            mTask.setCustomerId(selectedCustomer.getId());
                            mTask.setCustomerDisplayName(selectedCustomer.getDisplayedName());
                            mTask.setCustomerEmail(selectedCustomer.getEmail());

                            taskCustomerEmail.setText(selectedCustomer.getEmail());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<ResPartner>> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Internet connection lost", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void createNewTask(IGetDataService service, SharedPreferences sharedPreferences,
                               ProjectTask projectTask) {

        Call<Integer> result = service.newProjectTask(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                projectTask
        );

        result.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Task successfully created!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    projectTask.setId(response.body());

                    TaskInfoFragment taskInfoFragment = TaskInfoFragment.newInstance(projectTask.getId());
                    FragmentManager fm = getFragmentManager();
                    fm.popBackStackImmediate();
                    fm.beginTransaction()
                            .replace(R.id.content_frame, taskInfoFragment)
                            .addToBackStack(null)
                            .commit();

                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Task does not created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
