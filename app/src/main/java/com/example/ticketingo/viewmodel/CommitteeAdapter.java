package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Committee;
import com.example.ticketingo.view.CommitteeDetailActivity;
import java.util.List;

public class CommitteeAdapter extends RecyclerView.Adapter<CommitteeAdapter.CommitteeViewHolder> {
    private final Context context;
    private final List<Committee> committeeList;

    public CommitteeAdapter(Context context, List<Committee> committeeList) {
        this.context = context;
        this.committeeList = committeeList;
    }

    @NonNull
    @Override
    public CommitteeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You need to create this layout: res/layout/item_committee.xml
        View view = LayoutInflater.from(context).inflate(R.layout.activity_committee_detail, parent, false);
        return new CommitteeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommitteeViewHolder holder, int position) {
        Committee committee = committeeList.get(position);
        holder.name.setText(committee.getCommittee_name());
        holder.description.setText(committee.getDescription());

        // Use Glide to load the logo from the URL
        Glide.with(context)
                .load(committee.getLogoUrl())
                .into(holder.logo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommitteeDetailActivity.class);
            // Pass the committee name to the detail activity for fetching its events
            intent.putExtra("COMMITTEE_NAME", committee.getCommittee_name());
            intent.putExtra("COMMITTEE_LOGO", committee.getLogoUrl());
            intent.putExtra("COMMITTEE_DESC", committee.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return committeeList.size();
    }

    public static class CommitteeViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        ImageView logo;

        public CommitteeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assuming IDs in item_committee.xml
            name = itemView.findViewById(R.id.committee_name);
            description = itemView.findViewById(R.id.committee_description);
            logo = itemView.findViewById(R.id.committee_logo);
        }
    }
}