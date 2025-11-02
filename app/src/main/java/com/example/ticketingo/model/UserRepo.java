package com.example.ticketingo.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepo {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // Background thread pool
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public UserRepo() {
        if (auth.getCurrentUser() != null) {
            userLiveData.setValue(auth.getCurrentUser());
        }
    }

    public void logout() {
        auth.signOut();
        userLiveData.setValue(null);
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    //  LOGIN (runs off main thread)
    public void login(String email, String password) {
        executor.execute(() -> auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        db.collection("Users").document(user.getUid())
                                .get()
                                .addOnSuccessListener(document -> {
                                    if (document.exists()) {
                                        String role = document.getString("role");
                                        Log.d("UserRepo", "User role: " + role);
                                        userLiveData.setValue(user);
                                    } else {
                                        errorLiveData.setValue("User record not found in Firestore");
                                    }
                                })
                                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
                    } else {
                        errorLiveData.setValue("Login failed: user is null");
                    }
                })
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage())));
    }

    //  REGISTER (off main thread)
    public void register(String email, String password, String username) {
        executor.execute(() -> auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        userLiveData.setValue(user);
                        addUserToFirestore(user.getUid(), username, email);
                    }
                })
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage())));
    }

    //  Firestore user creation (also on background thread)
    private void addUserToFirestore(String uid, String name, String email) {
        executor.execute(() -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("email", email);
            userMap.put("name", name);
            userMap.put("role", "user");

            db.collection("Users").document(uid)
                    .set(userMap)
                    .addOnSuccessListener(avoid -> Log.d("Firestore", "✅ User added successfully"))
                    .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
        });
    }

    public MutableLiveData<String> getUserRole() {
        MutableLiveData<String> roleLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            roleLiveData.postValue("guest");
            return roleLiveData;
        }

        db.collection("Users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");
                        roleLiveData.postValue(role != null ? role : "user");
                    } else {
                        roleLiveData.postValue("user");
                    }
                })
                .addOnFailureListener(e -> roleLiveData.postValue("user"));

        return roleLiveData;
    }

    public MutableLiveData<String> makeUserAdmin(String emailToPromote) {
        MutableLiveData<String> result = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null || !currentUser.getEmail().equals("tiagala25@gmail.com")) {
            result.postValue("unauthorized");
            return result;
        }

        db.collection("Users")
                .whereEqualTo("email", emailToPromote)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String docId = snapshot.getDocuments().get(0).getId();
                        db.collection("Users").document(docId)
                                .update("role", "admin")
                                .addOnSuccessListener(aVoid -> result.postValue("success"))
                                .addOnFailureListener(e -> result.postValue("error"));
                    } else {
                        result.postValue("not_found");
                    }
                })
                .addOnFailureListener(e -> result.postValue("error"));

        return result;
    }

    public MutableLiveData<String> removeAdmin(String emailToDemote) {
        MutableLiveData<String> result = new MutableLiveData<>();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Only Superadmin (Tia) can perform this
        if (currentUser == null || !currentUser.getEmail().equals("tiagala25@gmail.com")) {
            result.postValue("unauthorized");
            return result;
        }

        db.collection("Users")
                .whereEqualTo("email", emailToDemote)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        String docId = snapshot.getDocuments().get(0).getId();
                        db.collection("Users").document(docId)
                                .update("role", "user")
                                .addOnSuccessListener(aVoid -> result.postValue("success"))
                                .addOnFailureListener(e -> result.postValue("error"));
                    } else {
                        result.postValue("not_found");
                    }
                })
                .addOnFailureListener(e -> result.postValue("error"));

        return result;
    }

    // Shutdown ExecutorService safely
    public void shutdownExecutor() {
        executor.shutdown();
        Log.d("UserRepo", "ExecutorService shut down.");
    }
}
