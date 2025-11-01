package com.example.ticketingo.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.example.ticketingo.model.Committee;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.viewmodel.CommitteeAdapterSimple;
import com.example.ticketingo.viewmodel.CommitteeViewModel;
import com.example.ticketingo.viewmodel.EventAdapter;
import com.example.ticketingo.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private RecyclerView committeesRecyclerView;
    private List<Event> eventList;
    private EventAdapter eventAdapter;
    private List<Committee> committeeList;
    private CommitteeAdapterSimple committeeAdapter;
    private CommitteeViewModel committeeViewModel;
    private EventViewModel eventViewModel;
    private AuthViewModel viewModel;

    private DrawerLayout drawerLayout;
    private ImageView profileIcon;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Drawer and Profile Icon
        drawerLayout = findViewById(R.id.drawer_layout);
        profileIcon = findViewById(R.id.profileIcon);

        // Initialize Auth ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // ---------- EVENTS RECYCLER VIEW ----------
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        eventsRecyclerView.setAdapter(eventAdapter);

        // Load events from ViewModel
        // Load events from ViewModel
        eventViewModel.loadEvents();
        eventViewModel.getEvents().observe(this, events -> {
            if (events != null) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.notifyDataSetChanged();
                // --- USE THE ADAPTER'S UPDATE METHOD TO POPULATE BOTH LISTS ---
//                eventAdapter.updateEvents(events);
                // Note: The two lines below are now redundant since updateEvents calls notifyDataSetChanged()
                // eventList.clear();
                // eventList.addAll(events);
                // eventAdapter.notifyDataSetChanged();
            }
        });

        // ---------- SEARCH FUNCTIONALITY ----------
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (eventAdapter != null) {
                    eventAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // ---------- COMMITTEES RECYCLER VIEW ----------
        committeesRecyclerView = findViewById(R.id.committeesRecyclerView);
        committeesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        committeeViewModel = new ViewModelProvider(this).get(CommitteeViewModel.class);

        committeeList = new ArrayList<>();
        committeeAdapter = new CommitteeAdapterSimple(this, committeeList);
        committeesRecyclerView.setAdapter(committeeAdapter);

        committeeViewModel.loadCommittees();
        committeeViewModel.getCommittees().observe(this, committees -> {
            if (committees != null) {
                committeeList.clear();
                committeeList.addAll(committees);
                committeeAdapter.notifyDataSetChanged();
            }
        });

        // ---------- PROFILE ICON CLICK LISTENER ----------
        profileIcon.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // ---------- BACK PRESS HANDLER ----------
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}
