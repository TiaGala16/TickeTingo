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

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminDashboardActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton3;
    private Button btnlogout;

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
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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