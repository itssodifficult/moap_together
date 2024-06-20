package com.example.myapplication;

import java.util.Map;

public class ChatRoom {
    private String roomId;
    private Map<String, ChatContext> chatData;

    public String getRoomId() {
            return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Map<String, ChatContext> getChatData() {
        return chatData;
    }

    public void setChatData(Map<String, ChatContext> chatData) {
        this.chatData = chatData;
    }
}