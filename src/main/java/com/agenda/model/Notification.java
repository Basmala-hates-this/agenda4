package com.agenda.model;

import java.time.LocalDateTime;

public class Notification {
    private int notif_id;
    private int user_id;
    private String message;
    private boolean lu;
    private LocalDateTime date;
    
    public Notification() {}
    
    public Notification(int notif_id, int user_id, String message, boolean lu, LocalDateTime date) {
        this.notif_id = notif_id;
        this.user_id = user_id;
        this.message = message;
        this.lu = lu;
        this.date = date;
    }
    
    // Getters and setters
    public int getNotif_id() { return notif_id; }
    public void setNotif_id(int notif_id) { this.notif_id = notif_id; }
    
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}