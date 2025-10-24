package com.example.ticketingo.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadStatus;
import com.example.ticketingo.utils.CloudinaryManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.callback.ErrorInfo;

import java.util.HashMap;
import java.util.Map;


public class EventRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<Boolean> uploadStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getUploadStatus() { return uploadStatus; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }

    public void createEvent(Context context, String title, String description,String organiser,
                            String date, double price, int totalTickets, Uri imageUri) {
        Log.d("EventRepo", " did we even receive the image????");
        if (imageUri == null) {
            Log.d("EventRepo", " did we even receive the image????");
            errorLiveData.setValue("Image is required");
            return;
        }

        CloudinaryManager.getInstance().init(context);
        Log.d("Event" , "did the api come back in EventRepo?");
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
                Log.d("EventRepo", "✅ Upload success!");
                String imageUrl = (String) resultData.get("secure_url");
                Log.d("EventRepo", "Image URL: " + imageUrl);
                createEventinFirestore(title, description, organiser, date, price, totalTickets, imageUrl);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e("EventRepo", "❌ Upload failed: " + error.getDescription());
                errorLiveData.setValue(error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.e("EventRepo", "Upload rescheduled: " + error.getDescription());
            }
        });
    }

    private void createEventinFirestore(String title, String description,String organiser, String date, double price, int totalTickets, String imageurl) {

        String eventid = db.collection("Events").document().getId();
        String emailid = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("EventRepo", "we did NOT receive the imageurl"+ emailid);

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("organiser" ,organiser);
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
}
