package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CalenderActivity extends AppCompatActivity implements EventAdapter.OnEventChangeListener {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private Button addEventButton;
    private int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);

        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.eventContainer);
        addEventButton = findViewById(R.id.addEventButton);

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                loadEventsForDate(year, month, dayOfMonth);
            }
        });

        addEventButton.setOnClickListener(v -> showAddEventDialog());
        Button setAlarmButton = findViewById(R.id.setAlarmButton);
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalenderActivity.this, CalenderAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadEventsForDate(int year, int month, int dayOfMonth) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getYear() == year && event.getMonth() == month && event.getDay() == dayOfMonth) {
                filteredEvents.add(event);
            }
        }
        eventAdapter.setFilteredEvents(filteredEvents); // 필터링된 이벤트 설정
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("이벤트 추가");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        EditText eventTitleEditText = dialogView.findViewById(R.id.eventTitleEditText);

        builder.setView(dialogView);

        builder.setPositiveButton("추가", (dialog, which) -> {
            String title = eventTitleEditText.getText().toString();
            int id = eventList.size() + 1;
            Event event = new Event(id, selectedYear, selectedMonth, selectedDay, title);
            eventList.add(event);
            loadEventsForDate(selectedYear, selectedMonth, selectedDay); // 추가 후 다시 필터링
            dialog.dismiss();
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onEventChange() {
        loadEventsForDate(selectedYear, selectedMonth, selectedDay);
    }
}