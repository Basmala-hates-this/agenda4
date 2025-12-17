package com.agenda.controler;

import com.agenda.model.User;
import com.agenda.model.Event;
import com.agenda.model.AgendaData;
import com.agenda.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;  
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unified Controller for the Agenda Application.
 * Manages both Users and Events using a single JSON data source.
 */
public class AgendaController {

    private static final String AGENDA_FILE = "data/agenda.json";

    private List<User> users;
    private List<Event> events;

    public AgendaController() {
        loadAgenda();
        if (users == null || users.isEmpty()) {
            users = new ArrayList<>();
            createDefaultUsers();
        }
        if (events == null) {
            events = new ArrayList<>();
        }
    }

    private void loadAgenda() {
        Type type = new TypeToken<AgendaData>() {}.getType();
        AgendaData data = JsonUtil.readFromFile(AGENDA_FILE, type);
        if (data == null) {
            data = new AgendaData();
        }
        this.users = data.getUsers() != null ? data.getUsers() : new ArrayList<>();
        this.events = data.getEvents() != null ? data.getEvents() : new ArrayList<>();
    }

    public void saveAgenda() {
        AgendaData data = new AgendaData();
        data.setUsers(users);
        data.setEvents(events);
        JsonUtil.writeToFile(AGENDA_FILE, data);
    }

    private void createDefaultUsers() {
        // Default admin account
        users.add(new User(1, "Admin", "User", "admin@agenda.com", "admin123", "0000000000", "admin"));
        saveAgenda();
    }

    // ============================
    // USER MANAGEMENT
    // ============================

    public User authenticate(String email, String password) {
        loadAgenda();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getPassword().equals(password.trim())) {
                return u;
            }
        }
        return null;
    }

    public boolean register(User user) {
        loadAgenda();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail())) return false;
        }
        user.setId(getNextUserId());
        users.add(user);
        saveAgenda();
        return true;
    }

    public List<User> getAllUsers() {
        loadAgenda();
        return users;
    }

    public void deleteUser(int userId) {
        users.removeIf(u -> u.getId() == userId);
        saveAgenda();
    }

    private int getNextUserId() {
        return users.stream().mapToInt(User::getId).max().orElse(0) + 1;
    }

    // ============================
    // EVENT MANAGEMENT (CRUD)
    // ============================

    public List<Event> getEvents() {
        loadAgenda();
        return events;
    }

    public void addEvent(Event e) {
        loadAgenda();
        e.setId(getNextEventId());
        events.add(e);
        saveAgenda();
    }

    /**
     * Modifies an existing event's details.
     */
    public boolean modifyEventDetails(Event original, String title, String time, String resp, String desc) {
        loadAgenda();
        for (Event e : events) {
            if (e.getId() == original.getId()) {
                e.setTitle(title);
                e.setTime(time);
                e.setResponsible(resp);
                e.setDescription(desc);
                saveAgenda();
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes an event from the list.
     */
    public boolean deleteEvent(Event e) {
        loadAgenda();
        boolean removed = events.removeIf(event -> event.getId() == e.getId());
        if (removed) {
            saveAgenda();
        }
        return removed;
    }

    private int getNextEventId() {
        return events.stream().mapToInt(Event::getId).max().orElse(0) + 1;
    }

    public Event getNextEvent() {
        // Logic to find the next chronological event could go here
        return events.isEmpty() ? null : events.get(0);
    }
    
    // Add these methods to your AgendaController class:

public List<User> getUsers() {
    loadAgenda();
    return new ArrayList<>(users); // Return copy
}

public void addUser(User user) {
    loadAgenda();
    users.add(user);
    saveAgenda();
}

public boolean updateEventDateTime(Event event, LocalDate newDate, String newTime) {
    loadAgenda();
    for (Event e : events) {
        if (e.getId() == event.getId()) {
            e.setDate(newDate);
            e.setTime(newTime);
            saveAgenda();
            return true;
        }
    }
    return false;
}
}