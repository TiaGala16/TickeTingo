package com.example.ticketingo.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.Ticket;
import com.example.ticketingo.model.TicketRepo;

import java.util.List;

public class TicketViewModel extends ViewModel {
    private final TicketRepo repo = new TicketRepo();

    public void createTicket(Context context, String date, String eventName, String location, boolean payment){
        repo.createTicket(context, date, eventName, location, payment);
    }

    public void loadTicket(){
        repo.loadTicket();
    }
    public LiveData<Boolean> getUploadStatus() { return repo.getUploadStatus(); }
    public LiveData<String> getError() { return repo.getErrorLiveData(); }
    public LiveData<List<Ticket>> getTickets(){ return repo.getTicketLiveData();}
}
