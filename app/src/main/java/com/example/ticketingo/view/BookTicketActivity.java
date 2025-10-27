package com.example.ticketingo.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.model.EventRepo;

import java.util.List;

public class BookTicketActivity extends AppCompatActivity {

    private ImageView eventImage;
    private TextView heading, eventDesc, location, date, price, contactInfoDetails;
    private Button bookNow;
    private EventRepo eventRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_ticket);

        // Handle system insets (edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize repository
        eventRepo = new EventRepo();

        // Initialize views
        eventImage = findViewById(R.id.eventImage);
        heading = findViewById(R.id.Heading);
        eventDesc = findViewById(R.id.EventDesc);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        price = findViewById(R.id.price);
        contactInfoDetails = findViewById(R.id.contactInfoDetails);
        bookNow = findViewById(R.id.bookNow);

        // Get event title from intent
        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");

        if (eventTitle != null && !eventTitle.isEmpty()) {
            // Load event data
            eventRepo.loadEvent(eventTitle);

            // Observe the event LiveData
            eventRepo.getEventsLiveData().observe(this, new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    if (events != null && !events.isEmpty()) {
                        // For example, show the first event or any specific one you need
                        Event event = events.get(0);

                        heading.setText(event.getTitle());
                        eventDesc.setText(event.getDescription());
                        location.setText(event.getLocation());
                        date.setText(event.getDate());
                        price.setText("₹" + event.getPrice());
                        contactInfoDetails.setText("ticketingo1234@gmail.com");

                        // Load event image with Glide
                        Glide.with(BookTicketActivity.this)
                                .load(event.getImageURL())
                                .placeholder(R.drawable.placeholder_image) // optional
                                .error(R.drawable.fantastic_four)
                                .into(eventImage);
                    }
                }
            });

        } else {
            heading.setText("Event Not Found");
            eventDesc.setText("No event details available.");
        }

        // Handle Book Now button click (example)
        bookNow.setOnClickListener(v -> {
            // You can navigate to a booking confirmation screen or payment activity here
            // Example: Toast message for now
            android.widget.Toast.makeText(this, "Booking functionality coming soon!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}
