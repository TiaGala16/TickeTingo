package com.example.ticketingo.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventName, DateEvent, eventPrice, TimeEvent, InfoEvent, totalticket, locationEdit;
    private Button selectImageBtn, createBtn;
    private Spinner spinner;
    private ImageView eventImage;
    private String timer = "";
    private String dates = "";
    private Uri imageUri;
    private EventViewModel viewModel;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        float ratio = (float) width / height;

                        Log.d("ImageCheck", "Width: " + width + " Height: " + height + " Ratio: " + ratio);

                        if (ratio >= 1.55f && ratio <= 2.3f) {
                            imageUri = uri;
                            eventImage.setImageURI(uri);
                            Toast.makeText(this, "Image accepted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please select an image with a 2:1 ratio", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
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

        // Initialize views
        eventName = findViewById(R.id.eventName);
        DateEvent = findViewById(R.id.DateEvent);
        eventPrice = findViewById(R.id.bookTicket);
        totalticket = findViewById(R.id.totalticket);
        InfoEvent = findViewById(R.id.InfoEvent);
        TimeEvent = findViewById(R.id.TimeEvent);
        spinner = findViewById(R.id.spinner);
        eventImage = findViewById(R.id.eventImage);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        createBtn = findViewById(R.id.createBtn);
        locationEdit = findViewById(R.id.location);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Spinner setup
        ArrayList<String> committees = new ArrayList<>();
        committees.add("Cultural");
        committees.add("Editorial");
        committees.add("Social Impact");
        committees.add("Technical and Research");
        committees.add("Sports");
        committees.add("Outreach");
        committees.add("Colloquium");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, committees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Image picker
        selectImageBtn.setOnClickListener(v -> imagePicker.launch("image/*"));
        // Date picker
        DateEvent.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreateEventActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        dates = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        DateEvent.setText(dates);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Time picker
        TimeEvent.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hr = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    CreateEventActivity.this,
                    (view, selectedHour, selectedMinute) -> {
                        timer = String.format("%02d:%02d", selectedHour, selectedMinute);
                        TimeEvent.setText(timer);
                    },
                    hr, min, true
            );
            timePickerDialog.show();
        });

        // Create button
        createBtn.setOnClickListener(v -> {
            String title = eventName.getText().toString().trim();
            String desc = InfoEvent.getText().toString().trim();
            String time = TimeEvent.getText().toString().trim();
            String date = DateEvent.getText().toString().trim();
            String location = locationEdit.getText().toString().trim();
            String inputOrg = spinner.getSelectedItem().toString().trim();

            double price;
            int totalTickets;
            try {
                price = Double.parseDouble(eventPrice.getText().toString().trim());
                totalTickets = Integer.parseInt(totalticket.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers for price and total tickets", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || desc.isEmpty() || inputOrg.isEmpty() || time.isEmpty() || date.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Fill all fields and select image", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("CreateEventActivity", "Creating event: " + title);
            viewModel.createEvent(CreateEventActivity.this, title, desc, time, inputOrg, date, price, totalTickets, imageUri, location);
        });

        // Observe upload status
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
