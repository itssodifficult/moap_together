package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView; // RecyclerView import 추가
import java.util.List;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> eventList;
    private List<Event> filteredEventList;
    private final Context context;
    private final OnEventChangeListener eventChangeListener;

    public interface OnEventChangeListener {
        void onEventChange();
    }

    public EventAdapter(List<Event> eventList, Context context, OnEventChangeListener eventChangeListener) {
        this.eventList = eventList;
        this.context = context;
        this.eventChangeListener = eventChangeListener;
        this.filteredEventList = new ArrayList<>(eventList);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredEvents(List<Event> filteredEvents) {
        this.filteredEventList = filteredEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = filteredEventList.get(position);
        holder.titleTextView.setText(event.getTitle());

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("이벤트 편집");

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_event, null);
            EditText eventTitleEditText = dialogView.findViewById(R.id.eventTitleEditText);
            eventTitleEditText.setText(event.getTitle());

            builder.setView(dialogView);

            builder.setPositiveButton("저장", (dialog, which) -> {
                String newTitle = eventTitleEditText.getText().toString();
                event.setTitle(newTitle);
                notifyItemChanged(holder.getAdapterPosition());
                eventChangeListener.onEventChange();
            });

            builder.setNegativeButton("삭제", (dialog, which) -> {
                eventList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, eventList.size());
                eventChangeListener.onEventChange();
            });

            builder.setNeutralButton("취소", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}