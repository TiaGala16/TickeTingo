package com.example.ticketingo.model;

import com.google.firebase.firestore.PropertyName;

public class Committee {
    private String committee_name;
    private String description;

    @PropertyName("logo")
    private String logoUrl;

    // Required empty constructor for Firestore
    public Committee() {}

    public Committee(String committee_name, String description, String logoUrl) {
        this.committee_name = committee_name;
        this.description = description;
        this.logoUrl = logoUrl;
    }

    public String getCommittee_name() {
        return committee_name;
    }

    public void setCommittee_name(String committee_name) {
        this.committee_name = committee_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @PropertyName("logo")
    public String getLogoUrl() {
        return logoUrl;
    }

    @PropertyName("logo")
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}