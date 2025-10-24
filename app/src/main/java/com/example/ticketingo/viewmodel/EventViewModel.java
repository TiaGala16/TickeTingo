package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.EventRepo;

public class EventViewModel extends ViewModel {
    private final EventRepo repo = new EventRepo();

    public void createEvent(Context context, String title, String description,String organiser,
                            String date, double price, int totalTickets, Uri imageUri) {
        repo.createEvent(context, title, description, organiser,date, price, totalTickets, imageUri);
    }

    public LiveData<Boolean> getUploadStatus() { return repo.getUploadStatus(); }
    public LiveData<String> getError() { return repo.getErrorLiveData(); }
}