package com.example.ticketingo.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ticketingo.utils.CloudinaryManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.callback.ErrorInfo;
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


public class EventRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> eventsLiveData = new MutableLiveData<>();

    public LiveData<List<Event>> getEventsLiveData() {
        return eventsLiveData;
    }

    public MutableLiveData<Boolean> getUploadStatus() { return uploadStatus; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }

    public void createEvent(Context context, String title, String description,String time,String organiser,
                            String date, double price, int totalTickets, Uri imageUri, String location) {
        if (imageUri == null) {
            errorLiveData.setValue("Image is required");
            return;
        }
        db.collection("Events")
                .whereEqualTo("title", title.trim())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Duplicate found
                        errorLiveData.setValue("An event with this name already exists!");
                    } else {

                        CloudinaryManager.getInstance().init(context);
                        //This part is for uploading the image in Cloudinary

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
                                Log.d("EventRepo", "Upload success");
                                String imageUrl = (String) resultData.get("secure_url");
                                Log.d("EventRepo", "Image URL: " + imageUrl);
                                createEventinFirestore(title, description, time, organiser, date, price, totalTickets, imageUrl, location);
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Log.e("EventRepo", " Upload failed: " + error.getDescription());
                                errorLiveData.setValue(error.getDescription());
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {
                                Log.e("EventRepo", "Upload rescheduled: " + error.getDescription());
                            }
                        });
                    }
                });
    }

    private void createEventinFirestore(String title, String description,String time,String organiser, String date, double price, int totalTickets, String imageurl, String location) {

        String eventid = db.collection("Events").document().getId();
        String emailid = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("EventRepo", "we did NOT receive the imageurl"+ emailid);

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("time",time);
        event.put("organiser" ,organiser);
        event.put("location", location);
        event.put("date", date);
        event.put("price", price);
        event.put("imageUrl", imageurl);
        event.put("totalTickets", totalTickets);
        event.put("soldTickets", 0);
        event.put("createdBy", emailid);

        db.collection("Events").document(eventid).set(event)
                .addOnSuccessListener(aVoid -> uploadStatus.setValue(true))
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
    }
    public void loadEvents(){
        db.collection("Events").addSnapshotListener((queryDocumentSnapshots, error) -> {
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
                eventsLiveData.setValue(upcomingEvents );
            }
        });
    }

    public void loadEvent(String eventName) {
        // Get the currently logged-in user's email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Query Firestore for tickets that belong to the given event and the current user
        db.collection("Events")
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
                        // Update LiveData with filtered tickets
                        eventsLiveData.setValue(eventList);

                        Log.d("EventRepo", "Loaded " + eventList.size() +
                                " tickets for event: " + eventName + " (email: " + email + ")");
                    } else {
                        Log.d("EventRepo", "No tickets found for event: " + eventName +
                                " and user: " + email);
                        eventsLiveData.setValue(Collections.emptyList());
                    }
                });
    }
    public void loadEventsByOrganiser(String organiserName) {
        db.collection("Events")
                // Filter the events by the 'organiser' field which is the committee name
                .whereEqualTo("organiser", organiserName)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("EventRepo", "Error loading events by organiser", error);
                        errorLiveData.setValue("Failed to load events for " + organiserName);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        List<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            event.setEventId(doc.getId());
                            eventList.add(event);
                        }
                        // Reuse the sorting logic
                        List<Event> upcomingEvents = getUpcomingSortedEvents(eventList);
                        eventsLiveData.setValue(upcomingEvents);
                    }
                });
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

    public MutableLiveData<Boolean> verifyEventById(String eventId) {
        MutableLiveData<Boolean> isValidEvent = new MutableLiveData<>();

        db.collection("Events")
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isValidEvent.setValue(true);
                    } else {
                        isValidEvent.setValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventRepo", "Error verifying event: ", e);
                    isValidEvent.setValue(false);
                });

        return isValidEvent;
    }

}