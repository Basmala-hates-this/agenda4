
///remove this class and dependecies
///replace it with the agenda controler in the project
///this is just shit
///shiiiiiiiiiiiiiiiiiiiiiiit

package com.agenda.controler;

import com.agenda.model.Event;
import com.agenda.model.JsonEvent;
import com.agenda.model.Notification;
import com.agenda.model.User;
import com.agenda.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventController {
    private static final String EVENTS_FILE = "data/events.json";
    private static final String NOTIFICATIONS_FILE = "data/notifications.json";
    
    private List<JsonEvent> jsonEvents;
    private List<Notification> notifications;
    private AuthController authController;
    private User currentUser;
    
    public EventController(AuthController authController, User currentUser) {
        this.authController = authController;
        this.currentUser = currentUser;
        loadEvents();
        loadNotifications();
    }
    
    private void loadEvents() {
        Type eventListType = new TypeToken<List<JsonEvent>>(){}.getType();
        jsonEvents = JsonUtil.readListFromFile(EVENTS_FILE, eventListType);
    }
    
    private void saveEvents() {
        JsonUtil.writeListToFile(EVENTS_FILE, jsonEvents);
    }
    
    private void loadNotifications() {
        Type notificationListType = new TypeToken<List<Notification>>(){}.getType();
        notifications = JsonUtil.readListFromFile(NOTIFICATIONS_FILE, notificationListType);
    }
    
    private void saveNotifications() {
        JsonUtil.writeListToFile(NOTIFICATIONS_FILE, notifications);
    }
    
    public List<Event> getEventsForCurrentUser() {
        List<Event> events = new ArrayList<>();
        
        for (JsonEvent jsonEvent : jsonEvents) {
            // Show event if: created by current user OR shared with current user
            if (jsonEvent.getCreateur_id() == currentUser.getId() || 
                (jsonEvent.getPartage_avec() != null && 
                 jsonEvent.getPartage_avec().contains(currentUser.getId()))) {
                
                Event event = convertToEvent(jsonEvent);
                events.add(event);
            }
        }
        
        return events;
    }
    
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        for (JsonEvent jsonEvent : jsonEvents) {
            events.add(convertToEvent(jsonEvent));
        }
        return events;
    }
    
    private Event convertToEvent(JsonEvent jsonEvent) {
        Event event = new Event(
            jsonEvent.getTitre(),
            jsonEvent.getDate(),
            jsonEvent.getHeure(),
            jsonEvent.getDescription(),
            jsonEvent.getResponsable()
        );
        event.setEventId(jsonEvent.getEvent_id());
        event.setCreateurId(jsonEvent.getCreateur_id());
        if (jsonEvent.getPartage_avec() != null) {
            event.setPartageAvec(new ArrayList<>(jsonEvent.getPartage_avec()));
        }
        return event;
    }
    
    private JsonEvent convertToJsonEvent(Event event) {
        JsonEvent jsonEvent = new JsonEvent();
        jsonEvent.setEvent_id(event.getEventId());
        jsonEvent.setTitre(event.getTitle());
        jsonEvent.setDate(event.getDate());
        jsonEvent.setHeure(event.getTime());
        jsonEvent.setDescription(event.getDescription());
        jsonEvent.setCreateur_id(event.getCreateurId());
        jsonEvent.setResponsable(event.getResponsible());
        jsonEvent.setPartage_avec(event.getPartageAvec());
        return jsonEvent;
    }
    
    public boolean addEvent(Event event) {
        try {
            int newId = getNextEventId();
            event.setEventId(newId);
            event.setCreateurId(currentUser.getId());
            
            JsonEvent jsonEvent = convertToJsonEvent(event);
            jsonEvents.add(jsonEvent);
            saveEvents();
            
            // Create notification for creator
            addNotification(currentUser.getId(), 
                "You created event '" + event.getTitle() + "'");
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateEvent(Event event) {
        try {
            for (int i = 0; i < jsonEvents.size(); i++) {
                if (jsonEvents.get(i).getEvent_id() == event.getEventId()) {
                    JsonEvent jsonEvent = convertToJsonEvent(event);
                    jsonEvents.set(i, jsonEvent);
                    saveEvents();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteEvent(Event event) {
        try {
            boolean removed = jsonEvents.removeIf(e -> e.getEvent_id() == event.getEventId());
            if (removed) {
                saveEvents();
            }
            return removed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean shareEvent(Event event, List<Integer> userIds) {
        try {
            for (JsonEvent jsonEvent : jsonEvents) {
                if (jsonEvent.getEvent_id() == event.getEventId()) {
                    jsonEvent.setPartage_avec(userIds);
                    saveEvents();
                    
                    // Create notifications for shared users
                    User creator = authController.getUserById(currentUser.getId());
                    for (Integer userId : userIds) {
                        if (userId != currentUser.getId()) { // Don't notify self
                            addNotification(userId,
                                creator.getNom() + " shared event '" + 
                                event.getTitle() + "' with you");
                        }
                    }
                    
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private int getNextEventId() {
        int maxId = 0;
        for (JsonEvent event : jsonEvents) {
            if (event.getEvent_id() > maxId) {
                maxId = event.getEvent_id();
            }
        }
        return maxId + 1;
    }
    
    private void addNotification(int userId, String message) {
        int newId = getNextNotificationId();
        Notification notification = new Notification(
            newId,
            userId,
            message,
            false,
            LocalDateTime.now()
        );
        notifications.add(notification);
        saveNotifications();
    }
    
    private int getNextNotificationId() {
        int maxId = 0;
        for (Notification notification : notifications) {
            if (notification.getNotif_id() > maxId) {
                maxId = notification.getNotif_id();
            }
        }
        return maxId + 1;
    }
    
    public List<Notification> getNotificationsForCurrentUser() {
        return notifications.stream()
            .filter(n -> n.getUser_id() == currentUser.getId())
            .collect(Collectors.toList());
    }
    
    public void markNotificationAsRead(int notificationId) {
        for (Notification notification : notifications) {
            if (notification.getNotif_id() == notificationId) {
                notification.setLu(true);
                break;
            }
        }
        saveNotifications();
    }
    
    public void markAllNotificationsAsRead() {
        for (Notification notification : notifications) {
            if (notification.getUser_id() == currentUser.getId()) {
                notification.setLu(true);
            }
        }
        saveNotifications();
    }
    
    public int getUnreadNotificationCount() {
        return (int) notifications.stream()
            .filter(n -> n.getUser_id() == currentUser.getId() && !n.isLu())
            .count();
    }
    
    public List<User> getAllUsersExceptCurrent() {
        return authController.getAllUsers().stream()
            .filter(u -> u.getId() != currentUser.getId())
            .collect(Collectors.toList());
    }
    
    public boolean canModifyEvent(Event event) {
        // Admin can modify any event
        if (currentUser.isAdmin()) {
            return true;
        }
        // User can only modify events they created
        return event.getCreateurId() == currentUser.getId();
    }
    
    public boolean canDeleteEvent(Event event) {
        // Admin can delete any event
        if (currentUser.isAdmin()) {
            return true;
        }
        // User can only delete events they created
        return event.getCreateurId() == currentUser.getId();
    }
}