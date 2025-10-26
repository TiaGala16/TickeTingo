package com.example.ticketingo.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.EventAdapter; // Reuse existing EventAdapter
import com.example.ticketingo.viewmodel.EventViewModel; // Reuse existing EventViewModel
import java.util.ArrayList;
import java.util.List;

public class CommitteeDetailActivity extends AppCompatActivity {

    private ImageView committeeLogo;
    private TextView committeeName, committeeDescription;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming your layout is activity_committee_detail.xml
        setContentView(R.layout.activity_committee_detail);

        // 1. Get data passed from the list activity
        String name = getIntent().getStringExtra("COMMITTEE_NAME");
        String logoUrl = getIntent().getStringExtra("COMMITTEE_LOGO");
        String description = getIntent().getStringExtra("COMMITTEE_DESC");

        // 2. Initialize Views
        committeeLogo = findViewById(R.id.committee_logo);
        committeeName = findViewById(R.id.committee_name);
        committeeDescription = findViewById(R.id.committee_description);
        eventsRecyclerView = findViewById(R.id.committee_events_recyclerview);

        // 3. Display Committee Details
        committeeName.setText(name);
        committeeDescription.setText(description);
        Glide.with(this)
                .load(logoUrl)
                .into(committeeLogo);

        // 4. Setup Events RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // You are reusing the existing EventAdapter [cite: 588]
        eventAdapter = new EventAdapter(this, eventList);
        eventsRecyclerView.setAdapter(eventAdapter);

        // 5. Fetch and Display Committee Events
        // You are reusing the existing EventViewModel [cite: 599]
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // **NEW LOGIC:** Call the modified load method.
        // You will need to add a loadEventsByOrganiser method to EventViewModel 
        // that calls the new method in EventRepo (see step 7 below).
        eventViewModel.loadEventsByOrganiser(name);

        eventViewModel.getEvents().observe(this, events -> {
            if (events != null) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Optional: Observe errors from EventViewModel
        eventViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Event Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}