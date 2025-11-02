package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.viewmodel.EventAdapter;
import com.example.ticketingo.viewmodel.EventViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {


    private FloatingActionButton addEventFab;
    private DrawerLayout drawer_layout;
    private ImageView profileIcon;
    private RecyclerView eventsRecyclerView;
    private EventViewModel eventViewModel;
    private EventAdapter adapter;
    private EditText searchEditText2;

    private final List<Event> eventList = new ArrayList<>();

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

        // ---------- Initialize Views ----------
        addEventFab = findViewById(R.id.addEventFab);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        profileIcon = findViewById(R.id.profileIcon);
        drawer_layout = findViewById(R.id.drawer_layout);
        searchEditText2 = findViewById(R.id.searchEditText2);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // ---------- Setup RecyclerView ----------
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this, eventList, "admin");
        eventsRecyclerView.setAdapter(adapter);

        // ---------- Load Events ----------
        eventViewModel.loadEvents();
        eventViewModel.getEvents().observe(this, events -> {
            if (events != null) {
                adapter.updateEvents(events);
                eventList.clear();
                eventList.addAll(events);
                adapter.notifyDataSetChanged();// refresh both lists
            }
        });

        // ---------- Search Functionality ----------
        searchEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString()); // call adapter filter function
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ---------- Profile Sidebar ----------
        profileIcon.setOnClickListener(v -> {
            if (drawer_layout != null) {
                drawer_layout.openDrawer(GravityCompat.END);
            }
        });

        // ---------- Add Event Button ----------
        addEventFab.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });
    }
    }


