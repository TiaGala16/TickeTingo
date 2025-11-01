package com.example.ticketingo.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.EventViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collections;

public class ScanTicketActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private DecoratedBarcodeView barcodeView;
    private TextView textResult;
    private FirebaseFirestore db;
    private EventViewModel eventViewModel;
    private String currentEventId;
    private boolean scanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ticket);

        barcodeView = findViewById(R.id.barcode_scanner);
        textResult = findViewById(R.id.textResult);
        db = FirebaseFirestore.getInstance();
        currentEventId = getIntent().getStringExtra("EVENT_ID");
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startScanning();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void startScanning() {
        barcodeView.getBarcodeView().setDecoderFactory(
                new DefaultDecoderFactory(Collections.singletonList(BarcodeFormat.QR_CODE))
        );
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result == null || scanned) return;

            scanned = true; // avoid multiple scans
            barcodeView.pause();

            String scannedData = result.getText();
            if (scannedData == null || scannedData.length() < 21) {
                textResult.setText("❌ Invalid QR data");
                textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                return;
            }

            String ticketId = scannedData.substring(0, 20);
            String eventId = scannedData.substring(20);
            Log.d("Ticket" , "This id was from QRcode" +eventId);
            Log.d("Event" , "This id was from QRcode" +currentEventId);

            if (eventId.equals(currentEventId)) {
                verifyTicket(ticketId);
            } else {
                textResult.setText("⚠️ Ticket not for this event");
                textResult.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
        }
    };

    private void verifyTicket(String ticketId) {
        DocumentReference ticketRef = db.collection("Tickets").document(ticketId);
        ticketRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean used = documentSnapshot.getBoolean("used");
                if (used != null && !used) {
                    // Ticket valid
                    ticketRef.update("used", true)
                            .addOnSuccessListener(aVoid -> {
                                textResult.setText("✅ Ticket Accepted");
                                textResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            })
                            .addOnFailureListener(e -> {
                                textResult.setText("❌ Error updating ticket");
                                textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            });
                } else {
                    textResult.setText("⚠️ Ticket Already Used");
                    textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            } else {
                textResult.setText("❌ Invalid Ticket");
                textResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error fetching ticket", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
