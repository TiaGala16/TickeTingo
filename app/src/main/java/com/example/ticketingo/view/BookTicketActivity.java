package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.ticketingo.model.TicketRepo;

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
                        Event event = events.get(0);

                        // Debugging Toast
                        Toast.makeText(BookTicketActivity.this,
                                "Loaded event: " + event.getTitle(),
                                Toast.LENGTH_SHORT).show();

                        // Populate UI with event details
                        heading.setText(event.getTitle());
                        eventDesc.setText(event.getDescription());
                        location.setText(event.getLocation());
                        date.setText(event.getDate());
                        price.setText("₹" + event.getPrice());

                        // Load event image with Glide
                        Glide.with(BookTicketActivity.this)
                                .load(event.getImageURL())
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.fantastic_four)
                                .into(eventImage);
                    } else {
                        heading.setText("Event Not Found");
                        eventDesc.setText("No event details available.");
                    }
                }
            });

        } else {
            heading.setText("Event Not Found");
            eventDesc.setText("No event details available.");
        }

        // Handle Book Now button click
        bookNow.setOnClickListener(v -> {
            TicketRepo ticketRepo = new TicketRepo();
            ticketRepo.createTicket(this, date.getText().toString(), eventTitle, location.getText().toString(), true);
            Intent intent = new Intent(BookTicketActivity.this, ShowTicketActivity.class);
            intent.putExtra("ticketTitle", eventTitle);
            startActivity(intent);

            Toast.makeText(this, "Booking functionality coming soon!", Toast.LENGTH_SHORT).show();

        });
    }
}
