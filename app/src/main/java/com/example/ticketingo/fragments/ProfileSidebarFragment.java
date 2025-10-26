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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticketingo.R;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.view.LogIn; // Assuming your login activity is named LogIn

public class ProfileSidebarFragment extends Fragment {

    private TextView sidebarUserName;
    private TextView sidebarUserEmail;
    private Button btnLogout;
    private AuthViewModel viewModel;

    // Placeholder data (Replace with actual data fetching logic from shared preferences or database)
    private String currentUserName = "Aliya Khan";
    private String currentUserEmail = "aliya.khan@uni.edu";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_sidebar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Initialize views
        sidebarUserName = view.findViewById(R.id.sidebarUserName);
        sidebarUserEmail = view.findViewById(R.id.sidebarUserEmail);
        btnLogout = view.findViewById(R.id.btnlogout);

        // Initialize ViewModel (needs to be initialized inside the Fragment for proper scope)
        // Ensure your AuthViewModel is accessible to the Fragment
        if (getActivity() != null) {
            viewModel = new ViewModelProvider(getActivity()).get(AuthViewModel.class);
        }

        // 2. Load and display user data
        loadUserData();

        // 3. Set Logout Button Listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
    }

    private void loadUserData() {
        // Here you would fetch data from the ViewModel or database.
        sidebarUserName.setText(currentUserName);
        sidebarUserEmail.setText(currentUserEmail);
    }

    private void handleLogout() {
        // 1. Close the sidebar/drawer
        if (getActivity() != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            }
        }

        // 2. LOGOUT LOGIC (Uses the existing logic from your old MainActivity)
        if (viewModel != null) {
            viewModel.logout();
        }
        Toast.makeText(getContext(), "You have logged out", Toast.LENGTH_SHORT).show();

        // 3. Navigate to Login screen
        Intent intent = new Intent(getActivity(), LogIn.class);
        startActivity(intent);

        // Close the current activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}