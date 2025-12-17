package com.agenda.model;

import java.time.LocalDate;
import java.util.List;

public class JsonEvent {
    private int event_id;
    private String titre;
    private LocalDate date;
    private String heure;
    private String description;
    private int createur_id;
    private String responsable;
    private List<Integer> partage_avec;
    
    public JsonEvent() {}
    
    // Getters and setters
    public int getEvent_id() { return event_id; }
    public void setEvent_id(int event_id) { this.event_id = event_id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getCreateur_id() { return createur_id; }
    public void setCreateur_id(int createur_id) { this.createur_id = createur_id; }
    
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    
    public List<Integer> getPartage_avec() { return partage_avec; }
    public void setPartage_avec(List<Integer> partage_avec) { this.partage_avec = partage_avec; }
}