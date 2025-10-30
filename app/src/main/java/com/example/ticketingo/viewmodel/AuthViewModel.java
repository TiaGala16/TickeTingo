package com.example.ticketingo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ticketingo.model.UserRepo;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void register(String email, String password, String username) {
        repository.register(email, password, username);
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

    // New: Fetch user data (name & email) from Firestore Users collection
    public LiveData<User> getUserData(String uid) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        userLiveData.setValue(new User(
                                name != null ? name : "User",
                                email != null ? email : ""
                        ));
                    } else {
                        userLiveData.setValue(new User("User", ""));
                    }
                })
                .addOnFailureListener(e -> userLiveData.setValue(new User("User", "")));
        return userLiveData;
    }

    // User model class
    public static class User {
        private String name;
        private String email;

        public User() {}

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
