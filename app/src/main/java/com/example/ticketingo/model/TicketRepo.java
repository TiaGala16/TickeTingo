package com.example.ticketingo.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void createTicket(Context context, String date, String eventName, String location, boolean payment, String time) {
        try {
            createTicketInFirestore(date, eventName, location, payment, time);
            Log.d("TicketRepo", "createTicket() called successfully for event: " + eventName);
        } catch (Exception e) {
            Log.e("TicketRepo", "Failed to create ticket: " + e.getMessage());
            errorLiveData.postValue(e.getMessage());
        }
    }

    private void createTicketInFirestore(String date, String eventName, String location, boolean payment, String time) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String ticketId = db.collection("Tickets").document().getId();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Log.d("TicketRepo", "Creating Firestore ticket for user: " + email + ", event: " + eventName);

        // 🔍 Fetch Event by name
        db.collection("Events")
                .whereEqualTo("title", eventName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String eventId = documentSnapshot.getId();
                        String imageURL = documentSnapshot.getString("imageUrl");

                        Map<String, Object> ticket = new HashMap<>();
                        ticket.put("id", ticketId);
                        ticket.put("date", date);
                        ticket.put("time", time);
                        ticket.put("email", email);
                        ticket.put("eventName", eventName);
                        ticket.put("location", location);
                        ticket.put("payment", payment);
                        ticket.put("used", false);
                        ticket.put("eventId", eventId);

                        if (imageURL != null && !imageURL.isEmpty()) {
                            ticket.put("imageURL", imageURL);
                            Log.d("TicketRepo", "Event image found: " + imageURL);
                        } else {
                            Log.w("TicketRepo", "imageURL is empty for event: " + eventName);
                        }

                        //Generate QR Code URL
                        String apiURL = "https://api.qrserver.com/v1/create-qr-code/?data=" +
                                ticketId + eventId + "&size=200x200&ecc=M&color=000000&bgcolor=ffffff";
                        ticket.put("qrCode", apiURL);

                        //Increment soldTickets
                        db.collection("Events").document(eventId)
                                .update("soldTickets", FieldValue.increment(1))
                                .addOnSuccessListener(aVoid ->
                                        Log.d("TicketRepo", "soldTickets incremented for " + eventName))
                                .addOnFailureListener(e ->
                                        Log.e("TicketRepo", "Failed to increment soldTickets: " + e.getMessage()));

                        // Finally save ticket
                        db.collection("Tickets").document(ticketId).set(ticket)
                                .addOnSuccessListener(aVoid -> {
                                    uploadStatus.postValue(true);
                                    Log.d("TicketRepo", "Ticket saved successfully for " + eventName);
                                })
                                .addOnFailureListener(e -> {
                                    errorLiveData.postValue(e.getMessage());
                                    Log.e("TicketRepo", "Failed to save ticket: " + e.getMessage());
                                });

                    } else {
                        Log.w("TicketRepo", "No event found with title: " + eventName);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TicketRepo", "Error fetching event: " + e.getMessage());
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
                        Log.e("TicketRepo", "Error loading tickets", error);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Ticket ticket = doc.toObject(Ticket.class);
                            ticket.setId(doc.getId());
                            ticketList.add(ticket);

                            // Debug log to verify Firestore data
                            Log.d("TicketRepo", "Loaded Ticket → ID: " + doc.getId()
                                    + " | Event: " + ticket.getEventName()
                                    + " | Date: " + ticket.getTicketdate());
                        }
                        //ticketLiveData.postValue(ticketList);
                        List<Ticket> upcomingTickets = getUpcomingSortedTickets(ticketList);
                        ticketLiveData.postValue(upcomingTickets);
                        Log.d("TicketRepo", "Loaded " + ticketList.size() + " tickets for " + email);
                    } else {
                        Log.w("TicketRepo", "No tickets found for " + email);
                        ticketLiveData.postValue(Collections.emptyList());
                    }
                });

    }

    private List<Ticket> getUpcomingSortedTickets(List<Ticket> ticketList) {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return ticketList.stream()
                .map(event -> {
                    try {
                        Date ticketDate = sdf.parse(event.getTicketdate().replace(" ", ""));
                        return new AbstractMap.SimpleEntry<>(event, ticketDate);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(entry -> entry != null && entry.getValue() != null && !entry.getValue().before(today))
                .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public void loadTicket(String eventName) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("TicketRepo", "loadTicket() called for event: " + eventName + " | user: " + email);

        db.collection("Tickets")
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.d("TicketRepo", "Firestore query executed. Result size: " + querySnapshot.size());
                    if (!querySnapshot.isEmpty()) {
                        List<Ticket> ticketList = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Log.d("TicketRepo", "Found ticket doc: " + document.getId());
                            Ticket ticket = document.toObject(Ticket.class);

                            if (ticket != null) {
                                ticket.setId(document.getId());
                                ticketList.add(ticket);

                                // Debug log for data validation
                                Log.d("TicketRepo", "Ticket Data → "
                                        + "ID: " + document.getId()
                                        + " | Event: " + ticket.getEventName()
                                        + " | Date: " + ticket.getTicketdate()
                                        + " | Location: " + ticket.getlocation());
                            }
                        }
                        ticketLiveData.postValue(ticketList);
                        Log.d("TicketRepo", "LiveData updated with " + ticketList.size() + " ticket(s)");
                    } else {
                        Log.w("TicketRepo", "No tickets found for event: " + eventName + " and user: " + email);
                        ticketLiveData.postValue(Collections.emptyList());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TicketRepo", "Firestore error: " + e.getMessage());
                    ticketLiveData.postValue(Collections.emptyList());
                });
    }

    public void checkTicket(Context context, String date, String eventName, String location, boolean payment, String time, TicketCreationCallback callback) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("Tickets")
                .whereEqualTo("email", email)
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Create new ticket
                        createTicket(context, date, eventName, location, payment, time);
                        callback.onTicketCreated();
                    } else {
                        Log.d("TicketRepo", "Ticket already exists for event: " + eventName);
                        Toast.makeText(context, "You’ve already booked this event!", Toast.LENGTH_SHORT).show();
                        callback.onTicketAlreadyExists();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("TicketRepo", "Error checking ticket: " + e.getMessage());
                    Toast.makeText(context, "Error checking ticket. Please try again.", Toast.LENGTH_SHORT).show();
                    callback.onError(e.getMessage());
                });
    }

}

