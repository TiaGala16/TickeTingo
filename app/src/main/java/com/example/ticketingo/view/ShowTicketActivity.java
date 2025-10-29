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
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.ticketingo.R;
import com.example.ticketingo.model.Ticket;
import com.example.ticketingo.model.TicketRepo;

import java.util.List;

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

        // Get event name from intent
        String ticketTitle = getIntent().getStringExtra("ticketTitle");

        if (ticketTitle != null && !ticketTitle.isEmpty()) {
            Log.d("ShowTicketActivity", "Loading ticket for: " + ticketTitle);

            // Initialize repo
            ticketRepo = new TicketRepo();

            // Load the ticket from Firestore
            ticketRepo.loadTicket(ticketTitle);

            // Observe LiveData from TicketRepo
            ticketRepo.getTicketLiveData().observe(this, new Observer<List<Ticket>>() {
                @Override
                public void onChanged(List<Ticket> tickets) {
                    if (tickets != null && !tickets.isEmpty()) {
                        Ticket ticket = tickets.get(0);

                        // Display ticket info
                        eventTitle.setText(ticket.getEventName());
                        eventLocation.setText(ticket.getlocation());
                        ticketName.setText(ticket.getEmail());
                        orderNumber.setText(ticket.getId());
                        eventDate.setText(ticket.getTicketdate());
                        eventTime.setText(ticket.getTime());

                        // Load event image
                        Glide.with(ShowTicketActivity.this)
                                .load(ticket.getImageURL())
                                //.placeholder(R.drawable.placeholder_image)
                                //.error(R.drawable.fantastic_four)
                                .into(eventImage);

                        // Load QR code if available
                        Glide.with(ShowTicketActivity.this)
                                .load(ticket.getQRCode())
                                //.placeholder(R.drawable.fantastic_four)
                                .into(qrCode);

                        Log.d("ShowTicketActivity", "Ticket loaded successfully: " + ticket.getEventName());
                    } else {
                        Log.d("ShowTicketActivity", "No tickets found for " + ticketTitle);
                    }
                }
            });
        } else {
            Log.e("ShowTicketActivity", "No ticket title received in intent.");
            eventTitle.setText("Ticket not found");
        }
    }
}
