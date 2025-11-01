package com.example.ticketingo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R; // Ensure this is correct for your R file
import com.example.ticketingo.view.AdminDashboardActivity;
import com.example.ticketingo.view.LogIn;
import com.example.ticketingo.viewmodel.AuthViewModel;

public class AdminProfileSidebarFragment extends Fragment {

    private TextView sidebarUserName;
    private TextView sidebarUserEmail;
    private Button btnLogout;
    AuthViewModel viewModel ;

    public AdminProfileSidebarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment using the new admin layout
        return inflater.inflate(R.layout.fragment_admin_profile_sidebar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize views
        sidebarUserName = view.findViewById(R.id.sidebarUserName);
        sidebarUserEmail = view.findViewById(R.id.sidebarUserEmail);
        btnLogout = view.findViewById(R.id.btnlogout);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        }

        // 2. Load user data (Example placeholders)
        // In a real app, you would fetch this from your data source/model
        loadAdminData();

        // 3. Set up the Log out button listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logout();
                Toast.makeText(requireContext(),"you have logged out ",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireContext(), LogIn.class);
                startActivity(intent);
            }
        });

    }

    private void loadAdminData() {
        if (viewModel != null) {
            viewModel.getUser().observe(getViewLifecycleOwner(), firebaseUser -> {
                if (firebaseUser != null) {
                    // Fetch user data from Firestore
                    viewModel.getUserData(firebaseUser.getUid()).observe(getViewLifecycleOwner(), user -> {
                        if (user != null) {
                            sidebarUserName.setText(user.getName() != null ? user.getName() : "User");
                            sidebarUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                        } else {
                            sidebarUserName.setText("User");
                            sidebarUserEmail.setText("");
                        }
                    });
                } else {
                    sidebarUserName.setText("Not logged in");
                    sidebarUserEmail.setText("");
                }
            });
        }
    }
}