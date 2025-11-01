package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.Event;
import com.example.ticketingo.model.EventRepo;

import java.util.List;

public class EventViewModel extends ViewModel {
    private final EventRepo repo = new EventRepo();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> soldOutStatus = new MutableLiveData<>();
    public void createEvent(Context context, String title, String description, String time, String organiser,
                            String date, double price, int totalTickets, Uri imageUri, String location) {
        repo.createEvent(context, title, description,time, organiser,date, price, totalTickets, imageUri, location);
    }

    public void checkIfEventSoldOut(String eventTitle) {
        repo.checkIfEventSoldOut(eventTitle, soldOutStatus, errorLiveData);
    }
    public void loadEvent(String eventName) {
        repo.loadEvent(eventName);
    }
    public void loadEvents(){
        repo.loadEvents();
    }
    public LiveData<Boolean> getUploadStatus() { return repo.getUploadStatus(); }
    public LiveData<String> getError() { return repo.getErrorLiveData(); }
    public LiveData<List<Event>> getEvents(){ return repo.getEventsLiveData();}
    public void loadEventsByOrganiser(String organiserName) {
        repo.loadEventsByOrganiser(organiserName);
    }
    public LiveData<Boolean> getSoldOutStatus() {
        return soldOutStatus;
    }

    public LiveData<Boolean> verifyEventById(String eventId) {
        return repo.verifyEventById(eventId);
    }
}