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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.view.LogIn;
import com.example.ticketingo.view.ShowAllTicketsActivity;
import com.example.ticketingo.viewmodel.AuthViewModel;

public class ProfileSidebarFragment extends Fragment {

    private TextView sidebarUserName;
    private TextView sidebarUserEmail;
    private Button btnLogout, showTicket;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_sidebar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sidebarUserName = view.findViewById(R.id.sidebarUserName);
        sidebarUserEmail = view.findViewById(R.id.sidebarUserEmail);
        btnLogout = view.findViewById(R.id.btnlogout);
        showTicket = view.findViewById(R.id.bookTicket);

        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        }

        loadUserData();

        btnLogout.setOnClickListener(v -> handleLogout());
        showTicket.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ShowAllTicketsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
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

    private void handleLogout() {
        if (getActivity() != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        }

        if (viewModel != null) {
            viewModel.logout();
        }

        Toast.makeText(getContext(), "You have logged out", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
