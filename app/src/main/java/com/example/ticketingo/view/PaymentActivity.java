// ==================== COMPLETE PaymentActivity.java ====================
// Location: app/src/main/java/com/example/ticketingo/view/PaymentActivity.java

package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.model.TicketCreationCallback;
import com.example.ticketingo.viewmodel.TicketViewModel;
import com.example.ticketingo.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView paymentEventName, paymentEventDate, paymentEventTime, paymentEventLocation;
    private TextView ticketPrice, convenienceFee, totalAmount;
    private RadioGroup paymentMethodGroup;
    private Button btnPayNow, btnCancelPayment;
    private FrameLayout loadingOverlay;
    private TextView loadingText;

    private TicketViewModel ticketViewModel;
    private TransactionViewModel transactionViewModel;

    // Event details from intent
    private String eventTitle, eventDate, eventTime, eventLocation;
    private double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize ViewModels
        ticketViewModel = new ViewModelProvider(this).get(TicketViewModel.class);
        transactionViewModel = new TransactionViewModel();

        // Initialize views
        paymentEventName = findViewById(R.id.paymentEventName);
        paymentEventDate = findViewById(R.id.paymentEventDate);
        paymentEventTime = findViewById(R.id.paymentEventTime);
        paymentEventLocation = findViewById(R.id.paymentEventLocation);
        ticketPrice = findViewById(R.id.ticketPrice);
        convenienceFee = findViewById(R.id.convenienceFee);
        totalAmount = findViewById(R.id.totalAmount);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        btnPayNow = findViewById(R.id.btnPayNow);
        btnCancelPayment = findViewById(R.id.btnCancelPayment);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingText = findViewById(R.id.loadingText);

        // Get data from intent
        Intent intent = getIntent();
        eventTitle = intent.getStringExtra("EVENT_TITLE");
        eventDate = intent.getStringExtra("EVENT_DATE");
        eventTime = intent.getStringExtra("EVENT_TIME");
        eventLocation = intent.getStringExtra("EVENT_LOCATION");
        price = intent.getDoubleExtra("EVENT_PRICE", 0.0);

        // Populate event details
        populateEventDetails();

        // Set up button listeners
        btnPayNow.setOnClickListener(v -> processPayment());
        btnCancelPayment.setOnClickListener(v -> {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void populateEventDetails() {
        paymentEventName.setText(eventTitle != null ? eventTitle : "N/A");
        paymentEventDate.setText(eventDate != null ? eventDate : "N/A");
        paymentEventTime.setText(eventTime != null ? eventTime : "N/A");
        paymentEventLocation.setText(eventLocation != null ? eventLocation : "N/A");

        // Calculate fees
        double convenience = price * 0.10; // 10% convenience fee
        double total = price + convenience;

        ticketPrice.setText("₹" + String.format("%.2f", price));
        convenienceFee.setText("₹" + String.format("%.2f", convenience));
        totalAmount.setText("₹" + String.format("%.2f", total));
    }

    private void processPayment() {
        // Get selected payment method
        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
        String paymentMethod;

        if (selectedId == R.id.radioUPI) {
            paymentMethod = "UPI";
        } else if (selectedId == R.id.radioCard) {
            paymentMethod = "Card";
        } else if (selectedId == R.id.radioNetBanking) {
            paymentMethod = "Net Banking";
        } else {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading overlay
        loadingOverlay.setVisibility(View.VISIBLE);
        loadingText.setText("Processing payment via " + paymentMethod + "...");

        // Simulate payment processing (2 seconds delay)
        new Handler().postDelayed(() -> {
            // Payment successful - now create the ticket
            createTicketAfterPayment();
        }, 2000);
    }

    private void createTicketAfterPayment() {
        loadingText.setText("Creating your ticket...");

        // Create ticket using TicketViewModel
        ticketViewModel.checkTicket(
                this,
                eventDate,
                eventTitle,
                eventLocation,
                true, // payment = true
                eventTime,
                new TicketCreationCallback() {
                    @Override
                    public void onTicketCreated() {
                        // Create transaction record
                        saveTransactionRecord();

                        // Hide loading
                        loadingOverlay.setVisibility(View.GONE);

                        // Navigate to PaymentSuccessActivity
                        Intent successIntent = new Intent(PaymentActivity.this, PaymentSuccessActivity.class);
                        successIntent.putExtra("EVENT_TITLE", eventTitle);
                        startActivity(successIntent);

                        // Finish PaymentActivity
                        finish();
                    }

                    @Override
                    public void onTicketAlreadyExists() {
                        // Hide loading
                        loadingOverlay.setVisibility(View.GONE);

                        Toast.makeText(PaymentActivity.this,
                                "You already have a ticket for this event!",
                                Toast.LENGTH_LONG).show();

                        // Navigate to existing ticket
                        Intent ticketIntent = new Intent(PaymentActivity.this, ShowTicketActivity.class);
                        ticketIntent.putExtra("ticketTitle", eventTitle);
                        startActivity(ticketIntent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        // Hide loading
                        loadingOverlay.setVisibility(View.GONE);

                        Toast.makeText(PaymentActivity.this,
                                "Error creating ticket: " + message,
                                Toast.LENGTH_LONG).show();
                        Log.e("PaymentActivity", "Ticket creation error: " + message);
                    }
                }
        );
    }

    private void saveTransactionRecord() {
        // Get current date and time
        String transactionDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a",
                Locale.getDefault()).format(new Date());

        // Get selected payment method
        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
        String paymentMethod = "Unknown";
        if (selectedId == R.id.radioUPI) {
            paymentMethod = "UPI";
        } else if (selectedId == R.id.radioCard) {
            paymentMethod = "Card";
        } else if (selectedId == R.id.radioNetBanking) {
            paymentMethod = "Net Banking";
        }

        // Calculate total amount (with convenience fee)
        double totalAmount = price + (price * 0.10);

        // Create transaction record
        transactionViewModel.createTransaction(
                eventTitle,
                eventDate,
                eventTime,
                totalAmount,
                paymentMethod,
                transactionDate
        );

        Log.d("PaymentActivity", "Transaction record created successfully");
    }

    @Override
    public void onBackPressed() {
        // Check if payment is in progress
        if (loadingOverlay.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "Please wait, processing payment...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog before going back
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }
}