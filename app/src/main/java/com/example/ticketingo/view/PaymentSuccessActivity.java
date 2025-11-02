package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketingo.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    private Button btnViewTicket;
    private String eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Get event title from intent
        eventTitle = getIntent().getStringExtra("EVENT_TITLE");

        // Initialize button
        btnViewTicket = findViewById(R.id.btnViewTicket);

        // Set click listener to view ticket
        btnViewTicket.setOnClickListener(v -> navigateToTicket());
    }

    private void navigateToTicket() {
        // Navigate to ShowTicketActivity to display the ticket
        Intent ticketIntent = new Intent(PaymentSuccessActivity.this, ShowTicketActivity.class);
        ticketIntent.putExtra("ticketTitle", eventTitle);
        startActivity(ticketIntent);

        // Finish this activity so user can't come back to success screen
        finish();
    }

    @Override
    public void onBackPressed() {
        // When back is pressed, go to MainActivity instead of payment screen
        Intent mainIntent = new Intent(PaymentSuccessActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}