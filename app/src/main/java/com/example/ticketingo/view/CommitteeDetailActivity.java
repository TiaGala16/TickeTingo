package com.example.ticketingo.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Committee;
import com.example.ticketingo.model.CommitteeRepo;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.EventAdapter;
import com.example.ticketingo.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class CommitteeDetailActivity extends AppCompatActivity {

    private ImageView committeeLogo;
    private TextView committeeName, committeeDescription;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private List<Event> eventList = new ArrayList<>();

    private CommitteeRepo committeeRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee_detail);

        // Initialize views
        committeeLogo = findViewById(R.id.committee_logo);
        committeeName = findViewById(R.id.committee_name);
        committeeDescription = findViewById(R.id.committee_description);
        eventsRecyclerView = findViewById(R.id.committee_events_recyclerview);

        // Initialize Repo
        committeeRepo = new CommitteeRepo();

        // Get the committee name from the previous activity
        String name = getIntent().getStringExtra("COMMITTEE_NAME");
        if (name == null || name.isEmpty()) {
            Toast.makeText(this, "No committee name received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load committee details
        committeeRepo.loadCommitee(name);

        // Observe the committee LiveData
        committeeRepo.getCommitteesLiveData().observe(this, new Observer<List<Committee>>() {
            @Override
            public void onChanged(List<Committee> committees) {
                if (committees != null && !committees.isEmpty()) {
                    Committee committee = committees.get(0);
                    committeeName.setText(committee.getCommittee_name());
                    committeeDescription.setText(committee.getDescription());

                    Glide.with(CommitteeDetailActivity.this)
                            .load(committee.getLogoUrl())
                            //.placeholder(R.drawable.committee_placeholder_icon)
                            .into(committeeLogo);
                } else {
                    Toast.makeText(CommitteeDetailActivity.this, "Committee not found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Setup RecyclerView for events
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this, eventList ,"user");
        eventsRecyclerView.setAdapter(eventAdapter);

        // Initialize EventViewModel
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Load events organized by this committee
        eventViewModel.loadEventsByOrganiser(name);

        // Observe the events LiveData
        eventViewModel.getEvents().observe(this, events -> {
            if (events != null) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
            }
        });

        // Observe possible errors
        eventViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Event Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
