package com.example.maks.odooprojects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maks.odooprojects.models.HREmployee;
import com.example.maks.odooprojects.models.HREmployeeCategory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ViewHolder> {

    private List<HREmployee> employeeList;
    private final Context context;
    private final LayoutInflater inflater;

    EmployeeListAdapter(Context context, List<HREmployee> employees){
        this.context = context;
        this.employeeList = employees;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(R.layout.employee_list_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HREmployee employee = employeeList.get(position);

        holder.employeeName.setText(employee.getName());
        holder.employeeJob.setText(employee.getJob());
        holder.employeeWorkPlace.setText(employee.getWorkLocation());
        holder.employeeAvatar.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24px);
        holder.employeeCategories.removeAllViews();

        for(HREmployeeCategory e : employee.getCategories()){
            TextView employeeCategory = new TextView(context);
            employeeCategory.setText(e.getName());
            employeeCategory.setTextSize(12f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,0);
            params.setMargins(0,0,8, 0);
            employeeCategory.setLayoutParams(params);

            holder.employeeCategories.addView(employeeCategory);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView employeeName;
        TextView employeeJob;
        TextView employeeWorkPlace;
        LinearLayout employeeCategories;
        ImageView employeeAvatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            employeeName = itemView.findViewById(R.id.employee_name_tv);
            employeeJob = itemView.findViewById(R.id.employee_job_tv);
            employeeWorkPlace = itemView.findViewById(R.id.employee_workplace_tv);
            employeeCategories = itemView.findViewById(R.id.employee_categories_ll);
            employeeAvatar = itemView.findViewById(R.id.employee_avatar_iv);
        }
    }
}
