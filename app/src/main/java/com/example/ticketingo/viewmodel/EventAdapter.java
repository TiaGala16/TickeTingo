package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ticketingo.model.Event;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.view.BookTicketActivity;
import com.example.ticketingo.view.ScanTicketActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private final Context context;
    private final List<Event> eventList;
    // Remove final: we will initialize this later or within an update method
    private List<Event> eventListFull;
    private String userRole = "user";

    public EventAdapter(Context context, List<Event> eventList, String userRole) {
        this.context = context;
        this.eventList = eventList;
        // DO NOT initialize eventListFull here, it will be empty!
        this.eventListFull = new ArrayList<>(); // Initialize as an empty list
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_homepage, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate());
        holder.organiser.setText("Organised by: " + event.getOrganiser());
        if("admin".equalsIgnoreCase(userRole))
        {
            holder.price.setText("Scan Now");
        }else {
            holder.price.setText("₹" + event.getPrice());
        }

        Glide.with(context)
                .load(event.getImageURL())
                .error(R.drawable.fantastic_four)
                .into(holder.image);

        holder.price.setOnClickListener(v -> {
            if ("admin".equalsIgnoreCase(userRole)) {
                Intent intent = new Intent(context, ScanTicketActivity.class);
                intent.putExtra("EVENT_ID", event.getEventId());
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, BookTicketActivity.class);
                intent.putExtra("EVENT_TITLE", event.getTitle());
                context.startActivity(intent);
            }

        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public void updateEvents(List<Event> newEvents) {
        // Clear both lists
        eventListFull.clear();
        eventList.clear();

        // Populate the full list copy (source for filtering)
        eventListFull.addAll(newEvents);

        // Populate the displayed list
        eventList.addAll(newEvents);

        notifyDataSetChanged();
    }
    // --------------------- SEARCH / FILTER METHOD ---------------------
    public void filter(String query) {
        query = query.toLowerCase().trim();
        eventList.clear();

        if (query.isEmpty()) {
            eventList.addAll(eventListFull);
        } else {
            for (Event event : eventListFull) {
                // Filter by title, organiser, or date
                if (event.getTitle().toLowerCase().contains(query) ||
                        event.getOrganiser().toLowerCase().contains(query) ||
                        event.getDate().toLowerCase().contains(query)) {
                    eventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }

    // --------------------- VIEW HOLDER ---------------------
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, organiser;
        Button price;
        ImageView image;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            organiser = itemView.findViewById(R.id.eventOrganiser);
            price = itemView.findViewById(R.id.bookTicket);
            image = itemView.findViewById(R.id.eventImage);
        }
    }
}
