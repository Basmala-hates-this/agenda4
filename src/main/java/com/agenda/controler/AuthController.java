package com.agenda.controler;

import com.agenda.model.User;
import com.agenda.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AuthController {

    private static final String USERS_FILE = "data/users.json";
    private List<User> users;

    public AuthController() {
        ensureDataFolderExists();
        loadUsers();

        // If no users exist, create default admin and user
        if (users.isEmpty()) {
            createDefaultUsers();
        }
    }

    // Ensures "data/" folder exists
    private void ensureDataFolderExists() {
        File folder = new File("data");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // Load users from JSON file
    private void loadUsers() {
        Type userListType = new TypeToken<List<User>>(){}.getType();
        users = JsonUtil.readListFromFile(USERS_FILE, userListType);
        if (users == null) {
            users = new ArrayList<>();
        }
    }

    // Save users to JSON file
    private void saveUsers() {
        JsonUtil.writeListToFile(USERS_FILE, users);
    }

    // Create default admin and regular user
    private void createDefaultUsers() {
        User admin = new User(1, "Admin", "Admin",  "admin@agenda.com", "admin123", "0000000000", "admin");
        User user = new User(2, "Regular", "User", "user@agenda.com", "user123", "1234567890", "user");
        users.add(admin);
        users.add(user);
        saveUsers();
    }

    // Authenticate user
    public User authenticate(String email, String password) {
        loadUsers(); // always reload latest JSON
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim()) &&
                user.getPassword().equals(password.trim())) {
                return user;
            }
        }
        return null;
    }

    // Check if email exists
    public boolean emailExists(String email) {
        loadUsers(); // ensure latest data
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }

    // Register new user with extended fields
    public boolean register(String firstName, String lastName, String email, String phone, String role, String password) {
        loadUsers();
        if (emailExists(email)) return false;

        int newId = getNextUserId();
        User newUser = new User(newId, firstName, lastName, email, password, phone, role);
        users.add(newUser);
        saveUsers();
        return true;
    }

    private int getNextUserId() {
        int maxId = 0;
        for (User user : users) {
            if (user.getId() > maxId) maxId = user.getId();
        }
        return maxId + 1;
    }

    // Getters
    public List<User> getAllUsers() { return users; }
    public User getUserById(int id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }
    public User getUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) return user;
        }
        return null;
    }
}
