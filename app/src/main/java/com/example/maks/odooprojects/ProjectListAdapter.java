package com.example.maks.odooprojects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maks.odooprojects.models.ProjectProject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    private List<ProjectProject> projectList;
    private final Context context;
    private final LayoutInflater inflater;


    public ProjectListAdapter(Context context, List<ProjectProject> projects) {
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectProject project = projectList.get(position);

        holder.projectName.setText(project.getName());
        holder.projectCustomers.setText(project.getPartner());
        holder.projectTasksNumber.setText(String.format("%d Tasks", project.getTasksCount()));

        if (project.getFavourite())
            holder.isFavouriteProject.setImageResource(R.drawable.ic_star_filled);
        else
            holder.isFavouriteProject.setImageResource(R.drawable.ic_star_border);

        holder.itemView.setOnClickListener(v -> {
            TasksTabbedFragment tasksTabbedFragment = TasksTabbedFragment.newInstance(project.getId());
           ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, tasksTabbedFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView projectName;
        TextView projectCustomers;
        TextView projectTasksNumber;
        ImageView isFavouriteProject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.project_name_tv);
            projectCustomers = itemView.findViewById(R.id.project_customers_tv);
            projectTasksNumber = itemView.findViewById(R.id.project_number_tasks_tv);
            isFavouriteProject = itemView.findViewById(R.id.project_add_to_favourites_iv);
        }
    }
}
