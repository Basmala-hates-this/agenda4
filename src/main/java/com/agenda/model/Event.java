package com.agenda.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private int eventId;
    private int createurId;
    private String title;
    private LocalDate date;
    private String time;
    private String description;
    private String responsible;
    private List<Integer> partageAvec;
    private List<String> participants;

    public Event() {
        this.partageAvec = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public Event(String title, LocalDate date, String time, String description, String responsible) {
        this();
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
        this.responsible = responsible;
    }

    // Getters and setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getCreateurId() { return createurId; }
    public void setCreateurId(int createurId) { this.createurId = createurId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }

    public List<Integer> getPartageAvec() { return partageAvec; }
    public void setPartageAvec(List<Integer> partageAvec) { this.partageAvec = partageAvec; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
    
    // For backward compatibility with AgendaController
    public int getId() { return eventId; }
    public void setId(int id) { this.eventId = id; }
    
    public int getCreatorId() { return createurId; }
    public void setCreatorId(int creatorId) { this.createurId = creatorId; }
}