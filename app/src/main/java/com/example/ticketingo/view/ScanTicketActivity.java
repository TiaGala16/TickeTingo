package com.example.ticketingo.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ticketingo.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanTicketActivity extends AppCompatActivity {

    Button btnScan;
    TextView textResult;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ticket);

        btnScan = findViewById(R.id.btnScan);
        textResult = findViewById(R.id.textResult);
        db = FirebaseFirestore.getInstance();

        btnScan.setOnClickListener(v -> startQRScanner());
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the Ticket QR Code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan(); // starts scanner
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String ticketId = result.getContents().trim();
                verifyTicket(ticketId);
            }
        }
    }

    private void verifyTicket(String ticketId) {
        DocumentReference ticketRef = db.collection("Tickets").document(ticketId);

        ticketRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean used = documentSnapshot.getBoolean("used");

                if (used != null && !used) {
                    // Ticket is valid and not used yet
                    ticketRef.update("used", true)
                            .addOnSuccessListener(aVoid -> {
                                textResult.setText("✅ Ticket Accepted");
                                textResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error updating ticket", Toast.LENGTH_SHORT).show());
                } else {
                    // Already used
                    textResult.setText("⚠️ Ticket Already Scanned");
                    textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            } else {
                textResult.setText("❌ Invalid Ticket");
                textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error fetching ticket data", Toast.LENGTH_SHORT).show());
    }
}
