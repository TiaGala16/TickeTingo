package com.example.ticketingo.model;

import com.google.firebase.firestore.PropertyName;

public class Event {
    private String title;
    private String date;
    private String time;
    private String organiser;
    private double price;
    private String description;
    private String createdBy;
    private int totalTickets;
    private int soldTickets;
    private String location;
    @PropertyName("imageUrl")
    private String imageURL;

    public Event(String title, String date, String organiser, double price, String imageURL) {
        this.title = title;
        this.date = date;
        this.organiser = organiser;
        this.price = price;
        this.imageURL = imageURL;
    }
    public Event() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

}
