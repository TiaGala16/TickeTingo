package com.example.ticketingo.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.Ticket;
import com.example.ticketingo.model.TicketCreationCallback;
import com.example.ticketingo.model.TicketRepo;

import java.util.List;

public class TicketViewModel extends ViewModel {
    private final TicketRepo repo = new TicketRepo();

    public void createTicket(Context context, String date, String eventName, String location, boolean payment, String time){
        repo.createTicket(context, date, eventName, location, payment, time);
    }
    public void loadTicket(String eventName){
        repo.loadTicket(eventName);
    }
    public void loadTicket(){
        repo.loadTickets();
    }

    public void checkTicket(Context context, String date, String eventName, String location, boolean payment, String time, TicketCreationCallback callback) {
        repo.checkTicket(context, date, eventName, location, payment, time, callback);
    }

    public LiveData<Boolean> getUploadStatus() { return repo.getUploadStatus(); }
    public LiveData<String> getError() { return repo.getErrorLiveData(); }
    public LiveData<List<Ticket>> getTickets(){ return repo.getTicketLiveData();}


}
