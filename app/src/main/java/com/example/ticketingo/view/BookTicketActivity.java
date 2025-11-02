package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.ticketingo.viewmodel.EventViewModel;

import java.util.List;

public class BookTicketActivity extends AppCompatActivity {

    private ImageView eventImage;
    private TextView heading, eventDesc, location, date, price, contactInfoDetails, time;
    private Button bookNow;
    private EventViewModel eventRepo;

    // Store current event for payment
    private Event currentEvent;

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
        eventRepo = new EventViewModel();

        // Initialize views
        eventImage = findViewById(R.id.eventImage);
        heading = findViewById(R.id.Heading);
        eventDesc = findViewById(R.id.EventDesc);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        price = findViewById(R.id.price);
        contactInfoDetails = findViewById(R.id.contactInfoDetails);
        bookNow = findViewById(R.id.bookNow);
        time = findViewById(R.id.time);

        // Get event title from intent
        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");

        if (eventTitle != null && !eventTitle.isEmpty()) {
            // Load event data
            eventRepo.loadEvent(eventTitle);

            // Observe the event LiveData
            eventRepo.getEvents().observe(this, new Observer<List<Event>>() {
                @Override
                public void onChanged(List<Event> events) {
                    if (events != null && !events.isEmpty()) {
                        currentEvent = events.get(0);

                        // Debugging Toast
                        Toast.makeText(BookTicketActivity.this,
                                "Loaded event: " + currentEvent.getTitle(),
                                Toast.LENGTH_SHORT).show();

                        // Populate UI with event details
                        heading.setText(currentEvent.getTitle());
                        eventDesc.setText(currentEvent.getDescription());
                        location.setText(currentEvent.getLocation());
                        date.setText(currentEvent.getDate());
                        price.setText("₹" + currentEvent.getPrice());
                        time.setText(currentEvent.getTime());

                        // Load event image with Glide
                        Glide.with(BookTicketActivity.this)
                                .load(currentEvent.getImageURL())
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
            if (eventTitle == null || eventTitle.isEmpty()) {
                Toast.makeText(this, "Event title is missing.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentEvent == null) {
                Toast.makeText(this, "Please wait, loading event details...", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if event is sold out
            eventRepo.checkIfEventSoldOut(eventTitle);
            eventRepo.getSoldOutStatus().observe(this, isSoldOut -> {
                if (Boolean.TRUE.equals(isSoldOut)) {
                    Toast.makeText(this, "This event is sold out", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if user already has a ticket for this event
                    checkExistingTicketAndProceed(eventTitle);
                }
            });
        });
    }

    private void checkExistingTicketAndProceed(String eventTitle) {
        // Use TicketViewModel to check if ticket already exists
        com.example.ticketingo.viewmodel.TicketViewModel ticketViewModel =
                new androidx.lifecycle.ViewModelProvider(this).get(com.example.ticketingo.viewmodel.TicketViewModel.class);

        ticketViewModel.loadTicket(eventTitle);
        ticketViewModel.getTickets().observe(this, tickets -> {
            if (tickets != null && !tickets.isEmpty()) {
                // Ticket already exists
                Toast.makeText(this, "You've already booked this event!", Toast.LENGTH_LONG).show();

                // Optionally navigate to the existing ticket
                Intent ticketIntent = new Intent(BookTicketActivity.this, ShowTicketActivity.class);
                ticketIntent.putExtra("ticketTitle", eventTitle);
                startActivity(ticketIntent);
            } else {
                // No existing ticket, proceed to payment
                Intent paymentIntent = new Intent(BookTicketActivity.this, PaymentActivity.class);
                paymentIntent.putExtra("EVENT_TITLE", currentEvent.getTitle());
                paymentIntent.putExtra("EVENT_DATE", currentEvent.getDate());
                paymentIntent.putExtra("EVENT_TIME", currentEvent.getTime());
                paymentIntent.putExtra("EVENT_LOCATION", currentEvent.getLocation());
                paymentIntent.putExtra("EVENT_PRICE", currentEvent.getPrice());
                startActivity(paymentIntent);
            }
        });
    }
}