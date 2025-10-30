package com.example.ticketingo.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
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

    public LiveData<List<Ticket>> getTicketLiveData() {
        return ticketLiveData;
    }

    public MutableLiveData<Boolean> getUploadStatus() {
        return uploadStatus;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // ✅ Create a ticket and save to Firestore
    public void createTicket(Context context, String date, String eventName, String location, boolean payment) {
        try {
            createTicketInFirestore(date, eventName, location, payment);
            Log.d("TicketRepo", "✅ createTicket() called successfully for event: " + eventName);
        } catch (Exception e) {
            Log.e("TicketRepo", "❌ Failed to create ticket: " + e.getMessage());
            errorLiveData.postValue(e.getMessage());
        }
    }

    private void createTicketInFirestore(String date, String eventName, String location, boolean payment) {
        String ticketId = db.collection("Tickets").document().getId();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Log.d("TicketRepo", "🎫 Creating Firestore ticket for user: " + email + ", event: " + eventName);

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("id", ticketId);
        ticket.put("date", date);
        ticket.put("email", email);
        ticket.put("eventName", eventName);
        ticket.put("location", location);
        ticket.put("payment", payment);
        ticket.put("used", false);

        // QR Code URL
        String apiURL = "https://api.qrserver.com/v1/create-qr-code/?data=" +
                ticketId + "&size=200x200&ecc=M&color=000000&bgcolor=ffffff";
        ticket.put("qrCode", apiURL);

        // Fetch image URL from Events collection
        db.collection("Events").document(eventName).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageURL = documentSnapshot.getString("imageURL");
                        ticket.put("imageURL", imageURL);
                        Log.d("TicketRepo", "📸 Event image found: " + imageURL);
                        //Toast.makeText(context, "Event image found: " + imageURL, Toast.LENGTH_SHORT).show();
                        // Increment soldTickets count
                        db.collection("Events").document(eventName)
                                .update("soldTickets", FieldValue.increment(1))
                                .addOnSuccessListener(aVoid ->
                                        Log.d("TicketRepo", "✅ soldTickets incremented for " + eventName))
                                .addOnFailureListener(e ->
                                        Log.e("TicketRepo", "❌ Failed to increment soldTickets: " + e.getMessage()));
                    } else {
                        Log.w("TicketRepo", "⚠️ Event not found: " + eventName);
                    }

                    // Save the ticket after fetching image
                    db.collection("Tickets").document(ticketId).set(ticket)
                            .addOnSuccessListener(aVoid -> {
                                uploadStatus.postValue(true);
                                Log.d("TicketRepo", "✅ Ticket saved successfully: " + ticketId);
                            })
                            .addOnFailureListener(e -> {
                                errorLiveData.postValue(e.getMessage());
                                Log.e("TicketRepo", "❌ Failed to save ticket: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("TicketRepo", "❌ Error fetching event: " + e.getMessage());
                    errorLiveData.postValue(e.getMessage());
                });
    }

    // ✅ Load all tickets for the current user
    public void loadTickets() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("TicketRepo", "🔍 Loading all tickets for user: " + email);

        db.collection("Tickets")
                .whereEqualTo("email", email)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("TicketRepo", "❌ Error loading tickets", error);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Ticket ticket = doc.toObject(Ticket.class);
                            ticket.setId(doc.getId()); // 👈 Firestore document ID
                            ticketList.add(ticket);

                            // Debug log to verify Firestore data
                            Log.d("TicketRepo", "📄 Loaded Ticket → ID: " + doc.getId()
                                    + " | Event: " + ticket.getEventName()
                                    + " | Date: " + ticket.getTicketdate());
                        }
                        ticketLiveData.postValue(ticketList);
                        Log.d("TicketRepo", "✅ Loaded " + ticketList.size() + " tickets for " + email);
                    } else {
                        Log.w("TicketRepo", "⚠️ No tickets found for " + email);
                        ticketLiveData.postValue(Collections.emptyList());
                    }
                });
    }

    // ✅ Load a specific ticket by event name for current user
    public void loadTicket(String eventName) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("TicketRepo", "🔍 loadTicket() called for event: " + eventName + " | user: " + email);

        db.collection("Tickets")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("TicketRepo", "📄 Firestore query executed. Result size: " + querySnapshot.size());
                    if (!querySnapshot.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("TicketRepo", "✅ Found ticket doc: " + document.getId());
                            Ticket ticket = document.toObject(Ticket.class);

                            if (ticket != null) {
                                ticket.setId(document.getId()); // 👈 Set Firestore document ID
                                ticketList.add(ticket);

                                // Debug log for data validation
                                Log.d("TicketRepo", "🎟 Ticket Data → "
                                        + "ID: " + document.getId()
                                        + " | Event: " + ticket.getEventName()
                                        + " | Date: " + ticket.getTicketdate()
                                        + " | Location: " + ticket.getlocation());
                            }
                        }
                        ticketLiveData.postValue(ticketList);
                        Log.d("TicketRepo", "✅ LiveData updated with " + ticketList.size() + " ticket(s)");
                    } else {
                        Log.w("TicketRepo", "⚠️ No tickets found for event: " + eventName + " and user: " + email);
                        ticketLiveData.postValue(Collections.emptyList());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TicketRepo", "❌ Firestore error: " + e.getMessage());
                    ticketLiveData.postValue(Collections.emptyList());
                });
    }
}
