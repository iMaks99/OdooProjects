package com.example.maks.odooprojects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maks.odooprojects.models.HRDepartment;
import com.example.maks.odooprojects.models.ProjectProject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DepartmentListAdapter extends RecyclerView.Adapter<DepartmentListAdapter.ViewHolder> {

    private List<HRDepartment> departmentList;
    private final Context context;
    private final LayoutInflater inflater;

    DepartmentListAdapter(Context context, List<HRDepartment> departments) {
        this.context = context;
        this.departmentList = departments;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(R.layout.department_list_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HRDepartment department = departmentList.get(position);

        holder.departmentName.setText(department.getName());

        holder.itemView.setOnClickListener( v -> {
            EmployeesRecyclerViewFragment employeesRecyclerViewFragment = EmployeesRecyclerViewFragment.newInstance(department.getId());
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, employeesRecyclerViewFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView departmentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            departmentName = itemView.findViewById(R.id.department_name_tv);
        }
    }
}
