package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<Event> eventList;
//    public EventAdapter(Context context , List<Event> eventList){
//    this.context =context;
//    this.eventList = eventList;
//    }

    public EventAdapter(Context context, List<Event> eventList) {
        this.context =context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
// In EventAdapter.onCreateViewHolder(), make sure you're using:
        View view = LayoutInflater.from(context).inflate(R.layout.admin_homepage, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate());
        holder.organiser.setText("Organised by: " + event.getOrganiser());
        holder.price.setText("₹" +event.getPrice());

        Glide.with(context).load(event.getImageURL()).error(R.drawable.fantastic_four).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, organiser;
        Button price;
        ImageView image;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            organiser = itemView.findViewById(R.id.eventOrganiser);
            price = itemView.findViewById(R.id.eventPrice);
            image = itemView.findViewById(R.id.eventImage);
        }
    }
}
