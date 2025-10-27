package com.example.ticketingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.ticketingo.model.Committee;
import com.example.ticketingo.model.CommitteeRepo;
import java.util.List;

public class CommitteeViewModel extends ViewModel {
    private final CommitteeRepo repo = new CommitteeRepo();

    public CommitteeViewModel() {
        // Load committees immediately when the ViewModel is created
        repo.loadCommittees();
    }
    public LiveData<List<Committee>> getCommittees() {
        return repo.getCommitteesLiveData();
    }

    public LiveData<String> getError() {
        return repo.getErrorLiveData();
    }

    public void loadCommittees() {
        repo.loadCommittees();
    }
}