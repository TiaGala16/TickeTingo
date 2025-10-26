package com.example.ticketingo.model;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommitteeRepo {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Committee>> committeesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Committee>> getCommitteesLiveData() {
        return committeesLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Collection name is assumed to be "Committees"
    public void loadCommittees() {
        db.collection("Committees")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e("CommitteeRepo", "Error loading committees", error);
                        errorLiveData.setValue("Failed to load committees: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<Committee> committeeList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Committee committee = doc.toObject(Committee.class);
                            committeeList.add(committee);
                        }
                        committeesLiveData.setValue(committeeList);
                    }
                });
    }
}