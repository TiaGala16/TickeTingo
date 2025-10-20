package com.example.ticketingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.UserRepo;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {
    private final UserRepo repository;
    private final LiveData<FirebaseUser> userLiveData;
    private final LiveData<String> errorLiveData;

    public AuthViewModel() {
        repository = new UserRepo();
        userLiveData = repository.getUserLiveData();
        errorLiveData = repository.getErrorLiveData();
    }

    public void login(String email, String password) {
        repository.login(email, password);
    }
    public void register(String email, String password,String username) {
        repository.register(email, password,username);
    }
    public void logout() {
        repository.logout();
    }

    public LiveData<FirebaseUser> getUser() {
        return userLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }
}
