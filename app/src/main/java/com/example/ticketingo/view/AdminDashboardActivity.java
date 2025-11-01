package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.EventAdapter;
import com.example.ticketingo.viewmodel.EventViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private FloatingActionButton addEventFab;
    EventAdapter adapter;
    private DrawerLayout drawer_layout;
    private ImageView profileIcon;
    RecyclerView eventsRecyclerView;
    private EventViewModel eventViewModel;
//    private AuthViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addEventFab = findViewById(R.id.addEventFab);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        profileIcon = findViewById(R.id.profileIcon);
        drawer_layout = findViewById(R.id.drawer_layout);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        List<Event> eventlist = new ArrayList<>();

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this,eventlist);
        eventsRecyclerView.setAdapter(adapter);

        //add to mine
        eventViewModel.loadEvents();

        eventViewModel.getEvents().observe(this,events -> {
            if(events!= null){
                eventlist.clear();
                eventlist.addAll(events);
                adapter.notifyDataSetChanged();
            }
        });
        profileIcon.setOnClickListener(v -> {
            if (drawer_layout != null) {
                drawer_layout.openDrawer(GravityCompat.END);
            }
        });
        addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

    }
}