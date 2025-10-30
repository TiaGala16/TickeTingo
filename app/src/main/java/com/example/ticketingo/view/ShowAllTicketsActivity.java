package com.example.ticketingo.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Ticket;
import com.example.ticketingo.viewmodel.AuthViewModel;
import com.example.ticketingo.viewmodel.TicketAdapter;
import com.example.ticketingo.viewmodel.TicketViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShowAllTicketsActivity extends AppCompatActivity implements TicketAdapter.OnShowTicketClickListener {

    RecyclerView ticketRecyclerView;
    TicketAdapter adapter;
    List<Ticket> ticketList;
    private TicketViewModel ticketViewModel;

    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_all_tickets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ticketRecyclerView = findViewById(R.id.ticketRecyclerView);
        ticketViewModel = new ViewModelProvider(this).get(TicketViewModel.class);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        ticketList = new ArrayList<>();

        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TicketAdapter(this, ticketList, this);
        ticketRecyclerView.setAdapter(adapter);

        ticketViewModel.loadTicket();

        ticketViewModel.getTickets().observe(this, tickets -> {
            if (tickets != null) {
                ticketList.clear();
                ticketList.addAll(tickets);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onShowTicketClick(String ticketTitle) {
        loadTicket(ticketTitle);
    }

    private void loadTicket(String ticketTitle) {
        // Example: start ShowTicketActivity
        Intent intent = new Intent(this, ShowTicketActivity.class);
        intent.putExtra("ticketTitle", ticketTitle);
        startActivity(intent);
    }
}
