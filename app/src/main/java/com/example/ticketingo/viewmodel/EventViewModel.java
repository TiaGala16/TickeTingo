package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.Event;
import com.example.ticketingo.model.EventRepo;

import java.util.List;

public class EventViewModel extends ViewModel {
    private final EventRepo repo=new EventRepo();
    //    private final LiveData<List<Event>> events;
//
//    public EventViewModel(){
//        repo =new EventRepo();
//        events = repo.getEventsLiveData();
//        repo.loadEvents;
//    }

    public void createEvent(Context context, String title, String description,String organiser,
                            String date, double price, int totalTickets, Uri imageUri) {
        repo.createEvent(context, title, description, organiser,date, price, totalTickets, imageUri);
    }

    public void loadEvents(){
        repo.loadEvents();
    }
    public LiveData<Boolean> getUploadStatus() { return repo.getUploadStatus(); }
    public LiveData<String> getError() { return repo.getErrorLiveData(); }
    public LiveData<List<Event>> getEvents(){ return repo.getEventsLiveData();}
}