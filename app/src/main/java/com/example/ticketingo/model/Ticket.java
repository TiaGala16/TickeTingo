package com.example.ticketingo.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;
public class Ticket {
    private String id;
    private String date;
    private String eventName;
    private String location;
    private boolean payment;
    private String imageURL;
    private String QRCode;
    private String email;
    private String time;
    private boolean used;


    public Ticket(String date, String eventName, String location, boolean payment, String time, boolean used) {
        this.date = date;
        this.eventName = eventName;
        this.location = location;
        this.payment = payment;
        this.time = time;
        this.used = used;
    }
    public Ticket() {}

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTicketDate() {
        return date;
    }

    public void setTicketDate(String date) {
        this.date = date;
    }

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    public boolean getpayment() {
        return payment;
    }

    public void setpayment(boolean payment) {
        this.payment = payment;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getQRCode() {
        return QRCode;
    }
    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public boolean getUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getId() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        String ticketid = db.collection("Tickets").document().getId();
        return ticketid;
    }
    public void setId(String id) {
        this.id = id;
    }
}
