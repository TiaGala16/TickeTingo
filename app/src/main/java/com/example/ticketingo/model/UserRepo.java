package com.example.ticketingo.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepo {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<FirebaseUser> userLiveData =  new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public UserRepo(){
        if(auth.getCurrentUser() != null){
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

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).
                addOnSuccessListener(result -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if(user!= null)
                    {
                        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(
                                document -> {
                                    if(document.exists()){
                                        String role = document.getString("role");
                                        if("admin".equals(role)){
                                            userLiveData.setValue(user);
                                        }else{
                                            userLiveData.setValue(user);
                                        }
                                    }
                                }
                        ).addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
//                        userLiveData.setValue(user);
                    }
                })
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
    }
    public void register(String email,String password,String username){
        auth.createUserWithEmailAndPassword(email,password).
                addOnSuccessListener(result ->
                {
                    FirebaseUser user = auth.getCurrentUser();
                    if(user!= null) {
                        userLiveData.setValue(auth.getCurrentUser());
                        addUsertoFirestore( user.getUid(), username,email);
                    }
                })
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));

    }

    private void addUsertoFirestore(String uid,String name, String email) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("role", "user");

        db.collection("Users").document(uid).set(userMap).addOnSuccessListener(avoid->{
                    Log.d("Firestore", "User added successfully");})
                .addOnFailureListener(e -> errorLiveData.setValue(e.getMessage()));
    }
}
