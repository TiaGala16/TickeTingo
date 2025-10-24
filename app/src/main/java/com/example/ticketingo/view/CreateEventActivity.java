package com.example.ticketingo.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.EventViewModel;

import java.util.ArrayList;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventName,DateEvent,eventPrice,InfoEvent,totalticket;
    private Button selectImageBtn,createBtn;
    private Spinner spinner;
    private ImageView eventImage;

    private Uri imageUri;
    private EventViewModel viewModel;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    eventImage.setImageURI(uri);
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eventName = findViewById(R.id.eventName);
        DateEvent = findViewById(R.id.DateEvent);
        eventPrice = findViewById(R.id.eventPrice);
        totalticket = findViewById(R.id.totalticket);
        InfoEvent = findViewById(R.id.InfoEvent);
        spinner = findViewById(R.id.spinner);
        eventImage = findViewById(R.id.eventImage);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        createBtn = findViewById(R.id.createBtn);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        ArrayList<String> Committee = new ArrayList<>();
        Committee.add("Cultural");
        Committee.add("Editorial");
        Committee.add("Social Impact");
        Committee.add("Technical and Research");
        Committee.add("Sports");
        Committee.add("Outreach");
        Committee.add("Colloquium");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Committee);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePicker.launch("image/*");
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CreateEventActivity", "Create button clicked!");
                String title = eventName.getText().toString().trim();
                String desc = InfoEvent.getText().toString().trim();
                String date = DateEvent.getText().toString().trim();
                String inputOrg = spinner.getSelectedItem().toString();
                double price = Double.parseDouble(eventPrice.getText().toString().trim());
                int totalTickets = Integer.parseInt(totalticket.getText().toString().trim());

                if (title.isEmpty() || desc.isEmpty() || date.isEmpty() || imageUri == null) {
                    Toast.makeText(CreateEventActivity.this, "Fill all fields and select image", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.createEvent(CreateEventActivity.this, title, desc,inputOrg, date, price, totalTickets, imageUri);
            }
        });
        viewModel.getUploadStatus().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
        });
    }
}