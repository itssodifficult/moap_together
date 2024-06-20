package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class CalenderAlarmActivity extends AppCompatActivity {

    private Spinner yearSpinner, monthSpinner, dateSpinner, timeSpinnerHour, timeSpinnerMinute;
    private EditText notificationEditText;
    private Button setAlarmButton;
    private AlarmManager alarmManager;
    private static final int REQUEST_PERMISSION_CODE = 123; // 알림 권한 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_alarm);

        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        dateSpinner = findViewById(R.id.dateSpinner);
        timeSpinnerHour = findViewById(R.id.timeSpinnerHour);
        timeSpinnerMinute = findViewById(R.id.timeSpinnerMinute);
        notificationEditText = findViewById(R.id.notificationEditText);
        setAlarmButton = findViewById(R.id.setAlarmButton);


        // Spinner 설정
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.years_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> dateAdapter = ArrayAdapter.createFromResource(this, R.array.dates_array, android.R.layout.simple_spinner_item);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);

        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(this, R.array.hours_array, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinnerHour.setAdapter(hourAdapter);

        ArrayAdapter<CharSequence> minuteAdapter = ArrayAdapter.createFromResource(this, R.array.minutes_array, android.R.layout.simple_spinner_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinnerMinute.setAdapter(minuteAdapter);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });
    }

    private void setAlarm() {
        String year = yearSpinner.getSelectedItem().toString();
        String month = monthSpinner.getSelectedItem().toString();
        String date = dateSpinner.getSelectedItem().toString();
        String hour = timeSpinnerHour.getSelectedItem().toString();
        String minute = timeSpinnerMinute.getSelectedItem().toString();
        String notificationMessage = notificationEditText.getText().toString();

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.YEAR, Integer.parseInt(year));
        alarmTime.set(Calendar.MONTH, Integer.parseInt(month) - 1); // 월은 0부터 시작하므로 1을 빼줌
        alarmTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date));
        alarmTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        alarmTime.set(Calendar.MINUTE, Integer.parseInt(minute));
        alarmTime.set(Calendar.SECOND, 0);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("notification_content", notificationMessage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "알람이 설정 불가합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSION_CODE);
                return;
            }
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);

        String alarmTimeString = year + "-" + month + "-" + date + " " + hour + ":" + minute;
        Toast.makeText(this, "알람이 " + alarmTimeString + "에 설정되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setAlarm();
            } else {
                Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}