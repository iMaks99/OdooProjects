package com.example.maks.odooprojects;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maks.odooprojects.models.MailActivity;
import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.example.maks.odooprojects.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskMailActivityDialogFragment extends DialogFragment {

    ProgressDialog progressDialog;

    public TaskMailActivityDialogFragment() {
        // Required empty public constructor
    }

    public static DialogFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt("task_id", id);

        DialogFragment dialogFragment = new TaskMailActivityDialogFragment();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_mail_activity_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading projects...");
        progressDialog.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        IGetDataService service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);

        Bundle args = getArguments();

        Call<List<MailActivity>> request = service.getTaskMailActivity(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                args.getInt("task_id")
        );

        request.enqueue(new Callback<List<MailActivity>>() {
            @Override
            public void onResponse(Call<List<MailActivity>> call, Response<List<MailActivity>> response) {
                progressDialog.dismiss();
                if (response.body().size() == 0)
                    dismiss();
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<MailActivity>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Ooops...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateDataList(List<MailActivity> body) {

        List<MailActivity> scheduled = new ArrayList<>();
        List<MailActivity> today = new ArrayList<>();
        List<MailActivity> overdue = new ArrayList<>();

        for(MailActivity mailActivity : body){
            if(DateUtils.isToday(mailActivity.getDateDeadline()))
                today.add(mailActivity);
            else if(DateUtils.isScheduled(mailActivity.getDateDeadline()))
                scheduled.add(mailActivity);
            else if(DateUtils.isOverdue(mailActivity.getDateDeadline()))
                overdue.add(mailActivity);
        }

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new MailActivityListAdapter(getContext(), scheduled, today, overdue));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getView().invalidate();
    }


}
