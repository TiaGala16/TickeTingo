package com.example.ticketingo.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.ticketingo.utils.CloudinaryManager;
import com.example.ticketingo.view.ShowTicketActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Ticket>> ticketLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Ticket>> getTicketLiveData() {return ticketLiveData;}
    public MutableLiveData<Boolean> getUploadStatus() { return uploadStatus; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }

    public void createTicket(Context context, String date, String eventName, String location, boolean payment) {
        try {
            // Directly create the ticket in Firestore
            createTicketinFirestore(date, eventName, location, payment);
            Log.d("TicketRepo", "✅ Ticket created successfully!");
        } catch (Exception e) {
            Log.e("TicketRepo", "❌ Failed to create ticket: " + e.getMessage());
            errorLiveData.setValue(e.getMessage());
        }
    }

    private void createTicketinFirestore(String date, String eventName, String location, boolean payment) {

        String ticketid = db.collection("Tickets").document().getId();
        Log.d("Ticetid", ticketid);
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("date", date);
        ticket.put("email", email);

        db.collection("Events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageURL = documentSnapshot.getString("imageURL");
                        Log.d("Firestore", "Image URL: " + imageURL);
                        ticket.put("imageURL", imageURL);

                        // ✅ Increment 'soldTickets' by 1
                        db.collection("Events").document(eventName)
                                .update("soldTickets", FieldValue.increment(1))
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "✅ soldTickets incremented for: " + eventName);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "❌ Failed to increment soldTickets: " + e.getMessage());
                                });

                    } else {
                        Log.e("Firestore", "❌ Document not found for: " + eventName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "❌ Error fetching document: " + e.getMessage());
                });

        ticket.put("eventName" ,eventName);
        ticket.put("location", location);
        ticket.put("payment", payment);
        String apiURL = "https://api.qrserver.com/v1/create-qr-code/?data="
                + ticketid + "&size=200x200&ecc=M&color=000000&bgcolor=ffffff";
        ticket.put("qrCode", apiURL);
        ticket.put("used", false);

        db.collection("Tickets").document(ticketid).set(ticket)
                .addOnSuccessListener(aVoid -> uploadStatus.setValue(true))
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
    }


    public void loadTicket() {
        // Get the currently logged-in user's email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Query Firestore for tickets belonging to that email
        db.collection("Tickets")
                .whereEqualTo("email", email)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("TicketRepo", "Error loading the tickets", error);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Ticket ticket = doc.toObject(Ticket.class);
                            ticketList.add(ticket);
                        }

                        // Update LiveData with this user's tickets
                        ticketLiveData.setValue(ticketList);

                        Log.d("TicketRepo", "Loaded " + ticketList.size() + " tickets for " + email);
                    } else {
                        Log.d("TicketRepo", "No tickets found for " + email);
                        ticketLiveData.setValue(Collections.emptyList());
                    }
                });
    }


    public void loadTicket(String eventName) {
        // Get the currently logged-in user's email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Step 1: Fetch ticket ID and details for the given event name and user
        db.collection("Tickets")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // ✅ Get existing ticket ID
                            String ticketId = document.getId();
                            Log.d("TicketID", "Ticket ID for event '" + eventName + "': " + ticketId);

                            // ✅ Convert Firestore document to Ticket object
                            Ticket ticket = document.toObject(Ticket.class);
                            if (ticket != null) {
                                ticketList.add(ticket);
                            }
                        }

                        // ✅ Update LiveData with the fetched tickets
                        ticketLiveData.setValue(ticketList);

                        Log.d("TicketRepo", "Loaded " + ticketList.size() +
                                " tickets for event: " + eventName + " (email: " + email + ")");
                    } else {
                        Log.d("TicketRepo", "No tickets found for event: " + eventName +
                                " and user: " + email);
                        ticketLiveData.setValue(Collections.emptyList());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching tickets", e);
                    ticketLiveData.setValue(Collections.emptyList());
                });
    }



}
