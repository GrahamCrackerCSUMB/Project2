package com.example.dogtraininglog.viewholders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogtraininglog.R;
import com.example.dogtraininglog.database.DogLog;

import java.util.List;

public class DogLogAdapter extends RecyclerView.Adapter<DogLogAdapter.DogLogViewHolder> {
    private List<DogLog> logs;

    public DogLogAdapter(List<DogLog> logs) {
        this.logs = logs;
    }

    public void updateList(List<DogLog> newLogs) {
        logs = newLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doglog_recycler_item, parent, false);
        return new DogLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogLogViewHolder holder, int position) {
        DogLog log = logs.get(position);
        holder.logTextView.setText(log.toString());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class DogLogViewHolder extends RecyclerView.ViewHolder {
        TextView logTextView;

        public DogLogViewHolder(@NonNull View itemView) {
            super(itemView);
            logTextView = itemView.findViewById(R.id.recyclerItemTextview);
        }
    }
}
