package com.example.ticketingo.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.view.LogIn;
import com.example.ticketingo.view.UpdateTicketsActivity;
import com.example.ticketingo.viewmodel.AuthViewModel;

public class AdminProfileSidebarFragment extends Fragment {

    private Button makeAdminBtn, btnLogout, addTickets;
    private TextView sidebarUserName, sidebarUserEmail;
    private AuthViewModel authViewModel;

    public AdminProfileSidebarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile_sidebar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        sidebarUserName = view.findViewById(R.id.sidebarUserName);
        sidebarUserEmail = view.findViewById(R.id.sidebarUserEmail);
        btnLogout = view.findViewById(R.id.btnlogout);
        addTickets = view.findViewById(R.id.addTickets);
        makeAdminBtn = view.findViewById(R.id.makeAdminBtn); // Must exist in your XML

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Load current admin data (name & email)
        loadAdminData();

        // Show "Make Admin" button only for superadmin (Tia)
        makeAdminBtn.setVisibility(View.GONE);
        authViewModel.getUserRole().observe(getViewLifecycleOwner(), role -> {
            if ("superadmin".equals(role)) {
                makeAdminBtn.setVisibility(View.VISIBLE);
            }
        });

        // Logout functionality
        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            Toast.makeText(requireContext(), "You have logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LogIn.class));
            requireActivity().finish();
        });

        // Add tickets button
        addTickets.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UpdateTicketsActivity.class);
            startActivity(intent);
        });

        // Make or remove admin access
        makeAdminBtn.setOnClickListener(v -> showManageAdminDialog());
    }

    // ------------------ Load Admin Profile Data ------------------
    private void loadAdminData() {
        authViewModel.getUser().observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                authViewModel.getUserData(firebaseUser.getUid()).observe(getViewLifecycleOwner(), user -> {
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

    // ------------------ Admin Management Dialog ------------------
    private void showManageAdminDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Manage Admin Access");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter user's email");
        input.setPadding(40, 30, 40, 30);
        builder.setView(input);

        // Option 1: Promote user to admin
        builder.setPositiveButton("Make Admin", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.makeUserAdmin(email).observe(getViewLifecycleOwner(), result ->
                    handleAdminResult(result, "granted admin access"));
        });

        // Option 2: Demote admin to user
        builder.setNegativeButton("Remove Admin", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.removeAdmin(email).observe(getViewLifecycleOwner(), result ->
                    handleAdminResult(result, "removed admin access"));
        });

        // Option 3: Cancel
        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    // ------------------ Result Toast Handler ------------------
    private void handleAdminResult(String result, String successMsg) {
        switch (result) {
            case "success":
                Toast.makeText(requireContext(), "Successfully " + successMsg, Toast.LENGTH_SHORT).show();
                break;
            case "not_found":
                Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
                break;
            case "unauthorized":
                Toast.makeText(requireContext(), "Only Tia can manage admins", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(requireContext(), "Error updating role", Toast.LENGTH_SHORT).show();
        }
    }
}
