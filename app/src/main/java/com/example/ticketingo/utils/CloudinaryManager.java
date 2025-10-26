package com.example.ticketingo.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.UploadCallback;
import com.example.ticketingo.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static CloudinaryManager instance;
    private boolean initialized = false;

    // Private constructor to prevent direct instantiation
    private CloudinaryManager() {}

    // Singleton instance method
    public static CloudinaryManager getInstance() {
        if (instance == null) {
            instance = new CloudinaryManager();
        }
        return instance;
    }

    // Initialize Cloudinary once
    public void init(Context context) {
        if (!initialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
            config.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
            MediaManager.init(context.getApplicationContext(), config);
            initialized = true;
        }
    }

    public void uploadImage(Uri imageUri, UploadCallback callback) {
        if (!initialized) {
            throw new IllegalStateException("CloudinaryManager not initialized! Call init(context) first.");
        }

        MediaManager.get()
                .upload(imageUri)
                .unsigned("unsigned_events_upload")
                .option("folder", "events_images")
                .callback(callback)
                .dispatch();
    }
}