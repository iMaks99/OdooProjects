package com.example.maks.odooprojects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maks.odooprojects.models.Colors;
import com.example.maks.odooprojects.models.ProjectProject;
import com.example.maks.odooprojects.network.IGetDataService;
import com.example.maks.odooprojects.network.RetrofitClientInstance;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    private List<ProjectProject> projectList;
    private final Context context;
    private final LayoutInflater inflater;
    int i;

    SharedPreferences sharedPreferences;
    IGetDataService service;

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

        sharedPreferences = context.getSharedPreferences("AuthPref", Context.MODE_PRIVATE);
        service = RetrofitClientInstance.getRetrofitInstance().create(IGetDataService.class);


        Call<ResponseBody> editRequest = service.editProject(
                sharedPreferences.getString("token", ""),
                sharedPreferences.getString("db_name", ""),
                projectList.get(position)
        );

        if (project.getFavourite())
            holder.isFavouriteProject.setImageResource(R.drawable.ic_star_filled);
        else
            holder.isFavouriteProject.setImageResource(R.drawable.ic_star_border);
        holder.isFavouriteProject.setOnClickListener(v -> {
            if (!project.getFavourite())
                projectList.get(position).setFavourite(true);
            else
                projectList.get(position).setFavourite(false);

            editTask(editRequest);
            notifyItemRangeChanged(position, projectList.size());
        });

        holder.projectColor.setBackgroundColor(Colors.getColor(project.getColor()));

        holder.showModalSheet.setOnClickListener(v -> {
            View view = inflater.inflate(R.layout.fragment_project_modal_bottom_sheet, null);

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(view);

            LinearLayout taskColors = view.findViewById(R.id.project_bottom_color_ll);
            Map<View, Integer> colorViewIdMap = new HashMap<>();
            for (i = 0; i < Colors.getColors().length; ++i) {
                View colorView = new View(context);
                colorView.setBackgroundColor(Colors.getColor(i));
                float scale = context.getResources().getDisplayMetrics().density;
                int pixels = (int) (50 * scale + 0.5f);
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(pixels,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                viewParams.setMarginEnd(4);
                colorView.setLayoutParams(viewParams);
                colorViewIdMap.put(colorView, i);
                colorView.setOnClickListener(c -> {
                    projectList.get(position).setColor(colorViewIdMap.get(colorView));
                    editProject(editRequest, position, dialog);
                });
                taskColors.addView(colorView);
            }

            TextView editProjectBtn = view.findViewById(R.id.project_bottom_edit_tv);
            editProjectBtn.setOnClickListener(b -> {
                EditProjectFragment editProjectFragment = EditProjectFragment.newInstance(project.getId());
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, editProjectFragment)
                        .addToBackStack(null)
                        .commit();
                dialog.dismiss();
            });

            dialog.show();
        });

        holder.itemView.setOnClickListener(v -> {
            TasksTabbedFragment tasksTabbedFragment = TasksTabbedFragment
                    .newInstance(project.getId(), project.getName());
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, tasksTabbedFragment, "projectTaskFragment")
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void editTask(Call<ResponseBody> request) {
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful())
                    Snackbar.make(((MainActivity) context).getCurrentFocus(), "Can't change project favourite, please check internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(((MainActivity) context).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void editProject(Call<ResponseBody> request, int position, BottomSheetDialog dialog) {
        request.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    notifyItemRangeChanged(position, projectList.size());
                    dialog.dismiss();
                } else {
                    Snackbar.make(((MainActivity) context).getCurrentFocus(), "Can't change project color, please check internet connection!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(((MainActivity) context).getCurrentFocus(), "Ooops...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
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
        View projectColor;
        ImageView showModalSheet;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            showModalSheet = itemView.findViewById(R.id.show_project_modal_sheet);
            projectName = itemView.findViewById(R.id.project_name_tv);
            projectCustomers = itemView.findViewById(R.id.project_customers_tv);
            projectTasksNumber = itemView.findViewById(R.id.project_number_tasks_tv);
            isFavouriteProject = itemView.findViewById(R.id.project_add_to_favourites_iv);
            projectColor = itemView.findViewById(R.id.project_color_v);
        }
    }
}
