package com.example.myapplication;

import java.util.List;

public class ConvenienceStoreResponse {
    private int currentCount;
    private List<Item> data;
    private int matchCount;
    private int page;
    private int perPage;
    private int totalCount;

    // Getter and setter for currentCount
    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    // Getter and setter for data
    public List<Item> getData() {
        return data;
    }

    public void setData(List<Item> data) {
        this.data = data;
    }

    // Getter and setter for matchCount
    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    // Getter and setter for page
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    // Getter and setter for perPage
    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    // Getter and setter for totalCount
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}