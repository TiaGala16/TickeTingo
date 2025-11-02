package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.browseractions.BrowserActionsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.EventViewModel;
import com.google.android.material.badge.BadgeUtils;

public class UpdateTicketsActivity extends AppCompatActivity {
    private Button btnticketsadd;
    private EditText addtick,eventname;
    private  EventViewModel eventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_tickets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eventname =findViewById(R.id.eventname);
        addtick= findViewById(R.id.addtick);
        btnticketsadd=findViewById(R.id.btnticketsadd);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);


        btnticketsadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = eventname.getText().toString().trim();
                String ticketsStr = addtick.getText().toString().trim();
                if (name.isEmpty() || ticketsStr.isEmpty()) {
                    Toast.makeText(UpdateTicketsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int ticketsToAdd = Integer.parseInt(ticketsStr);
                eventViewModel.addTicketsToEvent(name, ticketsToAdd)
                        .observe(UpdateTicketsActivity.this, result -> {
                            if(result.equals("success")){
                                Toast.makeText(UpdateTicketsActivity.this, "Tickets added successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(UpdateTicketsActivity.this, "Failed to update tickets", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}