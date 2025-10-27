package com.example.ticketingo.viewmodel;

import android.content.Context;
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

import java.util.List;

public class CommitteeAdapterSimple extends RecyclerView.Adapter<CommitteeAdapterSimple.CommitteeViewHolder> {

    private final Context context;
    private final List<Committee> committeeList;

    public CommitteeAdapterSimple(Context context, List<Committee> committeeList) {
        this.context = context;
        this.committeeList = committeeList;
    }

    @NonNull
    @Override
    public CommitteeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.committee_item_layout, parent, false);
        return new CommitteeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommitteeViewHolder holder, int position) {
        Committee committee = committeeList.get(position);

        // Set name
        holder.committeeName.setText(committee.getCommittee_name());

        // Set logo using Glide
        Glide.with(context)
                .load(committee.getLogoUrl())
                .placeholder(R.drawable.committee_placeholder_icon)
                .into(holder.committeeIcon);

        // Optional: handle click on item
        holder.itemView.setOnClickListener(v -> {
            // You can handle clicks here if needed
        });
    }

    @Override
    public int getItemCount() {
        return committeeList.size();
    }

    public static class CommitteeViewHolder extends RecyclerView.ViewHolder {
        ImageView committeeIcon;
        TextView committeeName;

        public CommitteeViewHolder(@NonNull View itemView) {
            super(itemView);
            committeeIcon = itemView.findViewById(R.id.committeeIcon);
            committeeName = itemView.findViewById(R.id.committeeName);
        }
    }
}
