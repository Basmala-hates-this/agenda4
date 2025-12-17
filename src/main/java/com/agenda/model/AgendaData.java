package com.agenda.model;

import java.util.ArrayList;
import java.util.List;

public class AgendaData {
    private List<User> users;
    private List<Event> events;

    public AgendaData() {
        this.users = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    // Getters & Setters
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }
}
