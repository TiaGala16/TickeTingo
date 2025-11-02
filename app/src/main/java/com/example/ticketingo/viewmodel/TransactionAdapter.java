package com.example.ticketingo.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketingo.R;
import com.example.ticketingo.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private final List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.eventNameText.setText(transaction.getEventName());
        holder.transactionDateText.setText(transaction.getTransactionDate());
        holder.amountText.setText("₹" + String.format("%.2f", transaction.getAmount()));
        holder.paymentMethodText.setText(transaction.getPaymentMethod());
        
        // Set status with color
        holder.statusText.setText(transaction.getStatus().toUpperCase());
        if ("success".equals(transaction.getStatus())) {
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if ("failed".equals(transaction.getStatus())) {
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameText, transactionDateText, amountText, paymentMethodText, statusText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.transactionEventName);
            transactionDateText = itemView.findViewById(R.id.transactionDate);
            amountText = itemView.findViewById(R.id.transactionAmount);
            paymentMethodText = itemView.findViewById(R.id.transactionPaymentMethod);
            statusText = itemView.findViewById(R.id.transactionStatus);
        }
    }
}