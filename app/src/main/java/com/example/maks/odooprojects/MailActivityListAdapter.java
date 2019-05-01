package com.example.maks.odooprojects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maks.odooprojects.models.MailActivity;
import com.example.maks.odooprojects.utils.DateUtils;

import java.util.List;

public class MailActivityListAdapter extends RecyclerView.Adapter<MailActivityListAdapter.ViewHolder> {

    private List<MailActivity> scheduled;
    private List<MailActivity> today;
    private List<MailActivity> overdue;
    private final Context context;
    private final LayoutInflater inflater;

    private int schSize;
    private int todaySize;
    private int overSize;

    public MailActivityListAdapter(Context context, List<MailActivity> scheduled, List<MailActivity> today, List<MailActivity> overdue) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

        this.scheduled = scheduled;
        this.today = today;
        this.overdue = overdue;

        schSize = scheduled != null ? scheduled.size() : 0;
        todaySize = today != null ? today.size() : 0;
        overSize = overdue != null ? overdue.size() : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                inflater.inflate(R.layout.task_mail_activity_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MailActivity mailActivity;

        if (position < overSize) {
            mailActivity = overdue.get(position);

            int days = DateUtils.getDaysDifference(mailActivity.getDateDeadline());
            if (days != -1)
                holder.mailActivityDeadline.setText(String.format("%d days overdue", days));
        } else if (position < todaySize + overSize && position >= overSize) {
            mailActivity = today.get(position);

            holder.mailActivityDeadline.setVisibility(View.GONE);
            holder.mailActivityTimeIcon.setVisibility(View.GONE);

        } else {
            mailActivity = scheduled.get(position);

            int days = DateUtils.getDaysDifference(mailActivity.getDateDeadline());
            if (days != -1)
                holder.mailActivityDeadline.setText(String.format("Due in %d days", days));
        }

        if (position == 0) {
            holder.mailTitleLayout.setVisibility(View.VISIBLE);
            holder.mailTitleName.setText("Overdue");
            holder.mailTitleName.setTextColor(0xba000d);
            holder.mailTitleCount.setText(String.valueOf(overSize));
        } else if (position == overSize) {
            holder.mailTitleLayout.setVisibility(View.VISIBLE);
            holder.mailTitleName.setText("Today");
            holder.mailTitleName.setTextColor(0xffeb3b);
            holder.mailTitleCount.setText(String.valueOf(todaySize));
        } else if (position == overSize + todaySize) {
            holder.mailTitleLayout.setVisibility(View.VISIBLE);
            holder.mailTitleName.setText("Planned");
            holder.mailTitleName.setTextColor(0x4caf50);
            holder.mailTitleCount.setText(String.valueOf(schSize));
        }

        holder.mailActivityType.setText(mailActivity.getActivityType());

        switch (mailActivity.getActivityType()) {
            case "Call":
                holder.mailActivityIcon.setImageResource(R.drawable.ic_round_call_24px);
                break;

            case "Exception":
                holder.mailActivityIcon.setImageResource(R.drawable.ic_round_report_problem_24px);
                break;

            case "Meeting":
                holder.mailActivityIcon.setImageResource(R.drawable.ic_round_group_work_24px);
                break;

            case "Email":
                holder.mailActivityIcon.setImageResource(R.drawable.ic_round_mail_24px);
                break;

            case "To Do":
                holder.mailActivityIcon.setImageResource(R.drawable.ic_round_assignment_24px);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return schSize + todaySize + overSize;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mailTitleLayout;
        TextView mailTitleName;
        TextView mailTitleCount;
        ImageView mailActivityIcon;
        ImageView mailActivityMarkdone;
        ImageView mailActivityEdit;
        TextView mailActivityDeadline;
        TextView mailActivityType;
        ImageView mailActivityTimeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mailTitleLayout = itemView.findViewById(R.id.mail_activity_title_ll);
            mailTitleName = itemView.findViewById(R.id.mail_activity_title_tv);
            mailTitleCount = itemView.findViewById(R.id.mail_activity_count_tv);
            mailActivityIcon = itemView.findViewById(R.id.mail_activity_icon_iv);
            mailActivityMarkdone = itemView.findViewById(R.id.mail_activity_markdone_iv);
            mailActivityEdit = itemView.findViewById(R.id.mail_activity_edit_iv);
            mailActivityDeadline = itemView.findViewById(R.id.mail_activity_deadline_tv);
            mailActivityType = itemView.findViewById(R.id.mail_activity_type_tv);
        }
    }
}
