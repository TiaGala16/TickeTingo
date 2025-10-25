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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EventAdapter adapter;
    List<Event> eventList;
    Button btnlogout;
    private AuthViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnlogout = findViewById(R.id.btnlogout);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        eventList = new ArrayList<>();
        String imageUrl = "android.resource://" + getPackageName() + "/" + R.drawable.fantastic_four;
        eventList.add(new Event("Cultural Night", "4th September 2025", "GDSC", 0, imageUrl));
        eventList.add(new Event("Music Fest", "10th October 2025", "College Union", 100.0,imageUrl));
        eventList.add(new Event("Tech Expo", "15th November 2025", "Tech Club", 250.0,imageUrl));

        adapter = new EventAdapter(this, eventList);
        recyclerView.setAdapter(adapter);

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logout();
                Toast.makeText(MainActivity.this,"you have logged out ",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
                finish();
            }
        });
    }
}