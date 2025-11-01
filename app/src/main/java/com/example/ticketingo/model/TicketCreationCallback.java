package com.example.ticketingo.model;

public interface TicketCreationCallback {
    void onTicketCreated();
    void onTicketAlreadyExists();
    void onError(String message);
}
