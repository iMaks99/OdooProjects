package com.example.maks.odooprojects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectProject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    private List<ProjectProject> projectList;
    private final Context context;
    private final LayoutInflater inflater;


    public ProjectListAdapter(Context context, List<ProjectProject> projects){
        this.context = context;
        this.projectList = projects;
        this.inflater = LayoutInflater.from(context);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(R.layout.project_list_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectProject project = projectList.get(position);

        holder.projectName.setText(project.getName());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView projectName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.project_name_tw);
        }
    }
}
