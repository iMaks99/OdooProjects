package com.example.maks.odooprojects;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProjectTaskFragment extends Fragment {

    public EditProjectTaskFragment() {
        // Required empty public constructor
    }

    public static EditProjectTaskFragment newInstance(int projectId) {
        Bundle args = new Bundle();
        args.putInt("project_id", projectId);
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

        Bundle args = getArguments();
        int projectId = args.getInt("project_id");

    }
}
