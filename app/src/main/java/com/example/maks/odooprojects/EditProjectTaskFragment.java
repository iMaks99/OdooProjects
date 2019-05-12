package com.example.maks.odooprojects;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProjectTaskFragment extends Fragment {

    ProjectTask mTask;
    List<ResPartner> mCompanyPartners;
    List<ResPartner> mPartnersAll;
    List<ProjectProject> mProjects;
    List<ProjectTaskType> mStages;
    List<ProjectTaskTag> mTags;
    Spinner taskStages;
    Spinner taskCustomers;
    ProjectTaskType taskStage;

    ImageView taskProgress;
    TextView taskProgressLegend;
    EditText taskCustomerEmail;

    public EditProjectTaskFragment() {
        // Required empty public constructor
    }

    public static EditProjectTaskFragment newInstance(int taskId) {
        Bundle args = new Bundle();
        args.putInt("task_id", taskId);
        EditProjectTaskFragment editProjectTaskFragment = new EditProjectTaskFragment();
        editProjectTaskFragment.setArguments(args);
        return editProjectTaskFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_project_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).createBackButton();
        ((MainActivity) getActivity()).setToolbarTitleEnabled("Editing task");


        Bundle args = getArguments();
        int task_id = args.getInt("task_id");

        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);

        Call<ProjectTask> request = service.getTaskById(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                task_id
        );

        request.enqueue(new Callback<ProjectTask>() {
            @Override
            public void onResponse(Call<ProjectTask> call, Response<ProjectTask> response) {
                if (response.isSuccessful()) {
                    mTask = response.body();

                    taskProgress = view.findViewById(R.id.new_project_task_progress_iv);
                    taskProgressLegend = view.findViewById(R.id.new_project_task_progress_name_tv);

                    TextView taskName = view.findViewById(R.id.new_project_task_name_ev);
                    taskName.setText(mTask.getName());

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

                    EditText taskDeadline = view.findViewById(R.id.new_project_task_deadline_ev);
                    if (mTask.getDeadline() != null) {
                        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
                        taskDeadline.setText(fmt.format(mTask.getDeadline()));
                    }

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

                    EditText taskDescription = view.findViewById(R.id.new_project_task_description_ev);
                    taskDescription.setText(HtmlCompat.fromHtml(mTask.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));

                    taskStages = view.findViewById(R.id.new_project_task_stage_sp);
                    ArrayAdapter<String> spinnerAdapterStages = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                    spinnerAdapterStages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    if (mStages == null)
                        getStages(spinnerAdapterStages, service, sharedPreferences, mTask.getProjectId());
                    else
                        for (ProjectTaskType stage : mStages)
                            spinnerAdapterStages.add(stage.getName());
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
                    taskProject.setSelection(spinnerAdapterProjects.getPosition(mTask.getProjectName()));
                    taskProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerAdapterStages.clear();
                            String selectedProject = taskProject.getItemAtPosition(position).toString();
                            int selectedProjectId = mProjects.stream()
                                    .filter(p -> p.getName() == selectedProject)
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

                                if (mTask.getTags() != null)
                                    tagChip.setChecked(mTask.getTags().stream()
                                            .filter(t -> t.getId() == tag.getId())
                                            .findFirst().orElse(null) != null);

                                tagChip.setOnCheckedChangeListener((ch, isSelecred) -> {

                                    ProjectTaskTag projectTaskTag = mTags.stream()
                                            .filter(t -> t.getName().equals(ch.getText().toString()))
                                            .findFirst()
                                            .orElse(null);

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

                    Spinner taskAssigned = view.findViewById(R.id.new_project_task_assigned_sp);
                    ArrayAdapter<String> spinnerAdapterPartner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                    spinnerAdapterPartner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    if (mCompanyPartners == null)
                        getUserPartners(spinnerAdapterPartner, service, sharedPreferences);
                    else
                        for (ResPartner partner : mCompanyPartners)
                            spinnerAdapterPartner.add(partner.getName());
                    taskAssigned.setAdapter(spinnerAdapterPartner);
                    taskAssigned.setSelection(spinnerAdapterPartner.getPosition(mTask.getAssignedTo()));
                    taskAssigned.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mTask.setAssignedTo(taskAssigned.getItemAtPosition(position).toString());

                            mTask.setAssignedToId(mCompanyPartners.stream()
                                    .filter(p -> p.getName().equals(mTask.getAssignedTo()))
                                    .findFirst()
                                    .orElse(null)
                                    .getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    taskCustomerEmail = view.findViewById(R.id.new_project_task_customer_email_ev);
                    if (mTask.getCustomerEmail() != null)
                        taskCustomerEmail.setText(mTask.getCustomerEmail());

                    taskCustomers = view.findViewById(R.id.new_project_task_customer_name_sp);
                    ArrayAdapter<String> spinnerAdapterCustomer = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
                    spinnerAdapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    getAllPartners(spinnerAdapterCustomer, service, sharedPreferences);

                    Button taskSave = view.findViewById(R.id.new_project_task_save_btn);
                    taskSave.setOnClickListener(v -> {
                        mTask.setName(taskName.getText().toString());
                        mTask.setCustomerEmail(taskCustomerEmail.getText().toString());
                        mTask.setDescription(HtmlCompat.toHtml(taskDescription.getText(), HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL));
                        editProjectTask(service, sharedPreferences);
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
            }

            @Override
            public void onFailure(Call<ProjectTask> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
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

                taskStage = mStages.stream()
                        .filter(s -> s.getId() == mTask.getStageId())
                        .findFirst()
                        .orElse(null);

                onTaskStageChanged();
                taskStages.setSelection(spinnerAdapterStages.getPosition(taskStage.getName()));

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

    private void getUserPartners(ArrayAdapter<String> stringArrayAdapter, IGetDataService service, SharedPreferences sharedPreferences) {
        Call<List<ResPartner>> request = service.getUserPartners(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", "")
        );

        request.enqueue(new Callback<List<ResPartner>>() {
            @Override
            public void onResponse(Call<List<ResPartner>> call, Response<List<ResPartner>> response) {
                mCompanyPartners = response.body();

                for (ResPartner partner : mCompanyPartners)
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
                    taskCustomers.setSelection(stringArrayAdapter.getPosition(mTask.getCustomerDisplayName()));
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

    private void editProjectTask(IGetDataService service, SharedPreferences sharedPreferences) {

        Call<ResponseBody> result = service.editProjectTask(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                mTask
        );

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Task successfully edited!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getFragmentManager().popBackStack();
                } else
                    Snackbar.make(getActivity().findViewById(R.id.content_frame), "Task does not edited", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.content_frame), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
