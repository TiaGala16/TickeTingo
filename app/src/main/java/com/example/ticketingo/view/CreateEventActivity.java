package com.example.ticketingo.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventName,DateEvent,eventPrice,TimeEvent,InfoEvent,totalticket;
    private Button selectImageBtn,createBtn;
    private Spinner spinner;
    private ImageView eventImage;
    String timer = "";
    String dates ="";
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

                        // Check if ratio is roughly 2:1 (allowing tiny float margin)
                        if (ratio >= 1.55f && ratio <= 2.3f) {
                            imageUri = uri;
                            eventImage.setImageURI(uri);
                            Toast.makeText(this, "Image accepted ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Please select an image with a 2:1 ratio", Toast.LENGTH_SHORT).show();
                        }
                    }catch (IOException e) {
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
        eventName = findViewById(R.id.eventName);
        DateEvent = findViewById(R.id.DateEvent);
        eventPrice = findViewById(R.id.eventPrice);
        totalticket = findViewById(R.id.totalticket);
        InfoEvent = findViewById(R.id.InfoEvent);
        TimeEvent = findViewById(R.id.TimeEvent);
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
        DateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year =calendar.get(Calendar.YEAR) ;
                int month =calendar.get(Calendar.MONTH);
                int day =calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datepickerdialog = new DatePickerDialog(
                        CreateEventActivity.this,(v,Selectedyear, Selectedmonth, Selectedyday) ->
                {
                    dates= Selectedyday +"/ " +(Selectedmonth+1) +"/ " +Selectedyear;
                    DateEvent.setText(dates);
                },
                        year,month,day
                );
                datepickerdialog.show();
            }
            });

        TimeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int hr = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timepickerdialog = new TimePickerDialog(
                        CreateEventActivity.this,(  v , selectedhour , selectedminute)->{
                    timer = selectedhour + ": " + selectedminute;
                    TimeEvent.setText(timer);
                } ,
                        hr, min,true
                );
                timepickerdialog.show();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CreateEventActivity", "Create button clicked!");
                String title = eventName.getText().toString().trim();
                String desc = InfoEvent.getText().toString().trim();
                String time = TimeEvent.getText().toString().trim();
                String date = DateEvent.getText().toString().trim();
                String inputOrg = spinner.getSelectedItem().toString();
                double price = Double.parseDouble(eventPrice.getText().toString().trim());
                int totalTickets = Integer.parseInt(totalticket.getText().toString().trim());

                if (title.isEmpty() || desc.isEmpty()||inputOrg.isEmpty() ||time.isEmpty() || date.isEmpty() || imageUri == null) {
                    Toast.makeText(CreateEventActivity.this, "Fill all fields and select image", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.createEvent(CreateEventActivity.this, title, desc,time,inputOrg, date, price, totalTickets, imageUri);
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