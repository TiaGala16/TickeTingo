package com.example.ticketingo.model;

public class Event {
    private String title;
    private String date;
    private String organiser;
    private String price;
    private String EventInfo;
    private String createdBy;
    private int totalTickets;
    private int soldTickets;
    private int imageResId;



    public Event(String title, String date, String organiser, String price, int imageResId) {
        this.title = title;
        this.date = date;
        this.organiser = organiser;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
    public String getEventInfo() {
        return EventInfo;
    }

    public void setEventInfo(String eventInfo) {
        EventInfo = eventInfo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

}
