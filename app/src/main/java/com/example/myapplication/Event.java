package com.example.myapplication;



public class Event {
    private int id;
    private int year;
    private int month;
    private int day;
    private String title;

    public Event(int id, int year, int month, int day, String title) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}