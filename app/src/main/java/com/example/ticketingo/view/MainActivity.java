package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback; // NEW: Import the correct class
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Event;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.viewmodel.EventAdapter;
//import com.example.ticketingo.viewmodel.CommitteeAdapter; // Added potential CommitteeAdapter import

import java.util.ArrayList;
import java.util.Arrays; // Added for committee data
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Existing fields
    RecyclerView eventsRecyclerView;
    RecyclerView committeesRecyclerView; // Added for completeness
    List<Event> eventList;
    private AuthViewModel viewModel;

    // Fields for Drawer Layout
    private DrawerLayout drawerLayout;
    private ImageView profileIcon;

    // Committee Data (Assuming a simple string list for demonstration)
    private final String[] COMMITTEE_NAMES = {
            "Cultural Committee", "Colloqium", "Editorial Board",
            "Outreach", "Social Impact", "Technical & Research cell",
            "Sports Committee"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout); // Ensure you have this ID in your main layout
        profileIcon = findViewById(R.id.profileIcon);

        // 2. Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 3. Setup Events RecyclerView
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Placeholder Data for Events RecyclerView
        eventList = new ArrayList<>();
        String imageUrl = "android.resource://" + getPackageName() + "/" + R.drawable.fantastic_four;
        eventList.add(new Event("Cultural Night", "4th September 2025", "GDSC", 0, imageUrl));
        eventList.add(new Event("Music Fest", "10th October 2025", "College Union", 100.0,imageUrl));
        eventList.add(new Event("Tech Expo", "15th November 2025", "Tech Club", 250.0,imageUrl));

        EventAdapter eventAdapter = new EventAdapter(this, eventList);
        eventsRecyclerView.setAdapter(eventAdapter);

        // 4. Setup Committees RecyclerView (Horizontal Slider)
        committeesRecyclerView = findViewById(R.id.committeesRecyclerView);
        committeesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Assuming you have a CommitteeAdapter that takes the list of names
        // CommitteeAdapter committeeAdapter = new CommitteeAdapter(this, Arrays.asList(COMMITTEE_NAMES));
        // committeesRecyclerView.setAdapter(committeeAdapter);

        // 5. Set Profile Icon Click Listener to open the drawer
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the drawer from the 'end' side (right side)
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        // 6. Implement OnBackPressedDispatcher (The fix for your error)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    // Close the drawer if it is open
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    // Otherwise, proceed with the default back navigation (finish the activity)

                    // NOTE: To avoid a redundant Toast and simplify logic,
                    // we'll remove the example Toast condition.

                    // To perform the default system back action:
                    setEnabled(false); // Disable this callback
                    getOnBackPressedDispatcher().onBackPressed(); // Call the system's default handler
                }
            }
        });
    }

    /**
     * NOTE: This method is now handled by the OnBackPressedDispatcher callback in onCreate.
     * The old, non-standard 'handleOnBackPressed' method has been removed/corrected.
     */
}