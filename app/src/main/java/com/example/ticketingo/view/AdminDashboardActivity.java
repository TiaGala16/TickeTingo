package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.viewmodel.EventAdapter;
import com.example.ticketingo.viewmodel.EventViewModel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton3;
    private Button btnlogout;
    EventAdapter adapter;
    RecyclerView recyclerView;
    private EventViewModel eventViewModel;
    private AuthViewModel viewModel;
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
        floatingActionButton3 = findViewById(R.id.floatingActionButton3);
        btnlogout=findViewById(R.id.btnlogout);
        recyclerView = findViewById(R.id.recyclerView);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        List<Event> eventlist = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(this,eventlist);
        recyclerView.setAdapter(adapter);

        //add to mine
        eventViewModel.loadEvents();

        eventViewModel.getEvents().observe(this,events -> {
            if(events!= null){
                eventlist.clear();
                eventlist.addAll(events);
                adapter.notifyDataSetChanged();
            }
        });


        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboardActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logout();
                Toast.makeText(AdminDashboardActivity.this,"you have logged out ",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(AdminDashboardActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }
}