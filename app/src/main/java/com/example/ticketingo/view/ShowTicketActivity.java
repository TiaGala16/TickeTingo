package com.example.ticketingo.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Ticket;
import com.example.ticketingo.model.TicketRepo;
import com.google.firebase.auth.FirebaseAuth;

public class ShowTicketActivity extends AppCompatActivity {

    private ImageView eventImage, qrCode;
    private TextView eventTitle, eventLocation, ticketName, orderNumber, eventDate, eventTime;

    private TicketRepo ticketRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        eventTitle = findViewById(R.id.eventTitle);
        eventLocation = findViewById(R.id.eventLocation);
        ticketName = findViewById(R.id.ticketName);
        orderNumber = findViewById(R.id.orderNumber);
        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);
        eventImage = findViewById(R.id.eventImage);
        qrCode = findViewById(R.id.qrCode);

        // Get data from intent
        String ticketTitle = getIntent().getStringExtra("ticketTitle");
        if (ticketTitle != null) {
            eventTitle.setText(ticketTitle);
        }

        // Initialize repo
        ticketRepo = new TicketRepo();

        // Load tickets matching this event and logged-in email
        loadTicket(ticketTitle);
    }

    private void loadTicket(String eventName) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        ticketRepo.getTicketLiveData().observe(this, tickets -> {
            if (tickets != null && !tickets.isEmpty()) {
                Ticket ticket = tickets.get(0); // get the first matching ticket
                // Display ticket details
                eventTitle.setText(ticket.getEventName());
                eventDate.setText(ticket.getTicketDate());
                eventLocation.setText(ticket.getlocation());
                ticketName.setText(ticket.getEmail());

                orderNumber.setText(ticket.getId());
                // assuming you have an 'id' field

                // Load event image
                Glide.with(this)
                        .load(ticket.getImageURL())
                        //.placeholder(R.drawable.placeholder)
                        .into(eventImage);

                // Generate QR code using the stored ticket ID
                String apiURL = "https://api.qrserver.com/v1/create-qr-code/?data="
                        + ticket.getId()
                        + "&size=200x200&ecc=M&color=000000&bgcolor=ffffff";
                Glide.with(this).load(apiURL).into(qrCode);

                Log.d("ShowTicketActivity", "✅ Ticket loaded successfully: " + ticket.getEventName());
            } else {
                Log.e("ShowTicketActivity", "❌ No ticket found for event: " + eventName);
            }
        });

        // Call repo function to actually fetch the ticket
        ticketRepo.loadTicket(eventName);
    }
}
