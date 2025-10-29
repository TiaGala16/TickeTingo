package com.example.ticketingo.model;

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

    @PropertyName("date")
    public String getTicketdate() {
        return date;
    }

    @PropertyName("date")
    public void setTicketdate(String date) {
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

    @PropertyName("imageURL")
    public String getImageURL() {
        return imageURL;
    }

    @PropertyName("imageURL")
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @PropertyName("qrCode")
    public String getQRCode() {
        return QRCode;
    }
    @PropertyName("qrCode")
    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    @PropertyName("time")
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
