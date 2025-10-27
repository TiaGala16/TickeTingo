package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final Context context;
    private final List<Ticket> ticketList;
    private final OnShowTicketClickListener listener;

    public interface OnShowTicketClickListener {
        void onShowTicketClick(String ticketTitle);
    }

    public TicketAdapter(Context context, List<Ticket> ticketList, OnShowTicketClickListener listener) {
        this.context = context;
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.ticketTitle.setText(ticket.getEventName());
        holder.ticketDate.setText(ticket.getTicketDate());

        holder.showTicket.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShowTicketClick(ticket.getEventName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView ticketTitle, ticketDate;
        Button showTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketTitle = itemView.findViewById(R.id.eventTitle);
            ticketDate = itemView.findViewById(R.id.eventDate);
            showTicket = itemView.findViewById(R.id.bookTicket);
        }
    }
}
