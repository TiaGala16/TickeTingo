package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.AuthViewModel;

public class SignUp extends AppCompatActivity {

    private AuthViewModel viewModel;
    private EditText emailInput,nameInput,confirmPasswordInput, passwordInput;
    private Button  registerButton;
    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel != null && viewModel.getUser().getValue() != null) {
            // user is already logged in, skip login
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        emailInput = findViewById(R.id.emailInput);
        confirmPasswordInput =findViewById(R.id.confirmPasswordInput);
        passwordInput = findViewById(R.id.passwordInput);
        nameInput= findViewById(R.id.nameInput);
        registerButton = findViewById(R.id.registerButton);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getUser().observe(this, user ->{
            if(user!=null){
                Toast.makeText(this,"Welcome" + user.getEmail(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewModel.getError().observe(this, error ->
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show()
        );


        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String useremail = emailInput.getText().toString();
                String userpass = passwordInput.getText().toString();
                String username = nameInput.getText().toString();
                String confirmpass = confirmPasswordInput.getText().toString();

                if (username.isEmpty() || useremail.isEmpty() || userpass.isEmpty() || confirmpass.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!useremail.endsWith("@nmims.in")){
                    Toast.makeText(SignUp.this,"Please enter a valid NMIMS mail ID",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!confirmpass.equals(userpass)) {
                    Toast.makeText(SignUp.this, "The Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                    viewModel.register(useremail,userpass,username);

            }
        });
    }
}