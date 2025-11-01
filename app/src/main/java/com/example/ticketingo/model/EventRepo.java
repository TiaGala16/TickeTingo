package com.example.ticketingo.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.ticketingo.utils.CloudinaryManager;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EventRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();

    // ✅ ExecutorService for background work
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public LiveData<List<Event>> getEventsLiveData() { return eventsLiveData; }
    public MutableLiveData<Boolean> getUploadStatus() { return uploadStatus; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
    public void createEvent(Context context, String title, String description, String time, String organiser,
                            String date, double price, int totalTickets, Uri imageUri, String location) {
        if (imageUri == null) {
            errorLiveData.postValue("Image is required");
            return;
        }
        executor.execute(() -> {
            db.collection("Events")
                    .whereEqualTo("title", title.trim())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            errorLiveData.postValue("An event with this name already exists!");
                        } else {
                            CloudinaryManager.getInstance().init(context);

                            CloudinaryManager.getInstance().uploadImage(imageUri, new UploadCallback() {
                                @Override
                                public void onStart(String requestId) {
                                    Log.d("EventRepo", "Upload started...");
                                }

                                @Override
                                public void onProgress(String requestId, long bytes, long totalBytes) {
                                    Log.d("EventRepo", "Uploading progress: " + bytes + "/" + totalBytes);
                                }

                                @Override
                                public void onSuccess(String requestId, Map resultData) {
                                    Log.d("EventRepo", "Upload success!");
                                    String imageUrl = (String) resultData.get("secure_url");
                                    Log.d("EventRepo", "Image URL: " + imageUrl);

                                    // Run Firestore save off the main thread
                                    executor.execute(() ->
                                            createEventInFirestore(title, description, time, organiser, date, price,
                                                    totalTickets, imageUrl, location)
                                    );
                                }

                                @Override
                                public void onError(String requestId, ErrorInfo error) {
                                    Log.e("EventRepo", "Upload failed: " + error.getDescription());
                                    errorLiveData.postValue(error.getDescription());
                                }

                                @Override
                                public void onReschedule(String requestId, ErrorInfo error) {
                                    Log.e("EventRepo", "Upload rescheduled: " + error.getDescription());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> errorLiveData.postValue("Error checking event: " + e.getMessage()));
        });
    }

    // ✅ Firestore Event Creation
    private void createEventInFirestore(String title, String description, String time, String organiser,
                                        String date, double price, int totalTickets, String imageUrl, String location) {
        try {
            String eventId = db.collection("Events").document().getId();
            String emailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            Map<String, Object> event = new HashMap<>();
            event.put("title", title);
            event.put("description", description);
            event.put("time", time);
            event.put("organiser", organiser);
            event.put("location", location);
            event.put("date", date);
            event.put("price", price);
            event.put("imageUrl", imageUrl);
            event.put("totalTickets", totalTickets);
            event.put("soldTickets", 0);
            event.put("createdBy", emailId);

            db.collection("Events").document(eventId).set(event)
                    .addOnSuccessListener(aVoid -> uploadStatus.postValue(true))
                    .addOnFailureListener(e -> errorLiveData.postValue("Failed to save event: " + e.getMessage()));
        } catch (Exception e) {
            errorLiveData.postValue("Error saving event: " + e.getMessage());
        }
    }
    public void loadEvents() {
        executor.execute(() -> db.collection("Events").addSnapshotListener((queryDocumentSnapshots, error) -> {
            if (error != null) {
                Log.e("EventRepo", "Error loading events", error);
                return;
            }

            if (queryDocumentSnapshots != null) {
                List<Event> eventList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Event event = doc.toObject(Event.class);
                    event.setEventId(doc.getId());
                    eventList.add(event);
                }
                List<Event> upcomingEvents = getUpcomingSortedEvents(eventList);
                eventsLiveData.postValue(upcomingEvents);
            }
        }));
    }
    public void loadEvent(String eventName) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        executor.execute(() -> db.collection("Events")
                .whereEqualTo("title", eventName)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("EventRepo", "Error loading ticket for event: " + eventName, error);
                        return;
                    }
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            eventList.add(event);
                        }
                        eventsLiveData.postValue(eventList);
                    } else {
                        eventsLiveData.postValue(Collections.emptyList());
                    }
                }));
    }
    public void loadEventsByOrganiser(String organiserName) {
        Log.d("EventRepo", "🔍 Loading events for organiser: '" + organiserName + "'");

        executor.execute(() -> db.collection("Events")
                .whereEqualTo("organiser", organiserName)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("EventRepo", "❌ Error loading events by organiser", error);
                        errorLiveData.postValue("Failed to load events for " + organiserName);
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        Log.d("EventRepo", "📦 Query returned " + queryDocumentSnapshots.size() + " documents");

                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            event.setEventId(doc.getId());
                            eventList.add(event);

                            // Debug log for each event
                            Log.d("EventRepo", "  ✅ Event: " + event.getTitle() +
                                    " | Organiser: '" + event.getOrganiser() + "'");
                        }

                        if (eventList.isEmpty()) {
                            Log.w("EventRepo", "⚠️ No events found for organiser: '" + organiserName + "'");
                        }

                        // Post ALL events without filtering by date
                        eventsLiveData.postValue(eventList);
                        Log.d("EventRepo", "✅ Posted " + eventList.size() + " events to LiveData");

                    } else {
                        Log.w("EventRepo", "⚠️ queryDocumentSnapshots is null");
                        eventsLiveData.postValue(new ArrayList<>());
                    }
                }));
    }
    private List<Event> getUpcomingSortedEvents(List<Event> eventList) {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return eventList.stream()
                .map(event -> {
                    try {
                        Date eventDate = sdf.parse(event.getDate().replace(" ", ""));
                        return new AbstractMap.SimpleEntry<>(event, eventDate);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(entry -> entry != null && entry.getValue() != null && !entry.getValue().before(today))
                .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public void shutdownExecutor() {
        executor.shutdown();
        Log.d("EventRepo", "ExecutorService shut down.");
    }
    public MutableLiveData<Boolean> verifyEventById(String eventId) {
        MutableLiveData<Boolean> isValidEvent = new MutableLiveData<>();
        db.collection("Events") .document(eventId) .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isValidEvent.setValue(true);
                    } else {
                        isValidEvent.setValue(false);
                    } }) .addOnFailureListener(e -> {
                        Log.e("EventRepo", "Error verifying event: ", e);
                        isValidEvent.setValue(false);
                    });
        return isValidEvent;
    }
}
