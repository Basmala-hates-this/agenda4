package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;
import javax.swing.*;
import java.awt.*;

public class UserProfilePanel extends JPanel {
    private AgendaController controller;
    private User currentUser;
    
    // Colors
    private final Color HEADER_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(100, 180, 255);
    
    public UserProfilePanel(AgendaController controller, User currentUser) {
        this.controller = controller;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        initUI();
    }
    
    private void initUI() {
        // Main container with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // User Info Card
        JPanel infoPanel = createUserInfoPanel();
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Statistics
        JPanel statsPanel = createUserStatsPanel();
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Preferences
        JPanel prefsPanel = createPreferencesPanel();
        mainPanel.add(prefsPanel);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(HEADER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_COLOR);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFirstName() + "!");
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(welcomeLabel);
        
        // Avatar/Profile picture placeholder
        JPanel avatarPanel = new JPanel();
        avatarPanel.setBackground(PANEL_COLOR);
        avatarPanel.setLayout(new BorderLayout());
        
        JLabel avatarLabel = new JLabel("👤", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));
        
        JLabel avatarText = new JLabel("Profile", SwingConstants.CENTER);
        avatarText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        avatarPanel.add(avatarLabel, BorderLayout.CENTER);
        avatarPanel.add(avatarText, BorderLayout.SOUTH);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(avatarPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Personal Information");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Info grid
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 15, 10));
        infoGrid.setBackground(PANEL_COLOR);
        
        infoGrid.add(createInfoLabel("Full Name:"));
        infoGrid.add(createInfoValue(currentUser.getNom()));
        
        infoGrid.add(createInfoLabel("Email:"));
        infoGrid.add(createInfoValue(currentUser.getEmail()));
        
        infoGrid.add(createInfoLabel("Phone:"));
        infoGrid.add(createInfoValue(currentUser.getPhone()));
        
        infoGrid.add(createInfoLabel("Account Type:"));
        infoGrid.add(createInfoValue(currentUser.getRole().equalsIgnoreCase("admin") ? "Administrator" : "Regular User"));
        
        JButton editBtn = new JButton("Edit Profile");
        styleButton(editBtn, ACCENT_COLOR);
        editBtn.addActionListener(e -> editProfile());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(editBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(infoGrid, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUserStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(BG_COLOR);
        
        panel.add(createStatCard("📅", "Events Created", "24", HEADER_COLOR));
        panel.add(createStatCard("👥", "Shared Events", "12", new Color(218, 165, 32)));
        panel.add(createStatCard("🔔", "Notifications", "8", new Color(100, 180, 255)));
        
        return panel;
    }
    
    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("Preferences");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel prefsGrid = new JPanel(new GridLayout(3, 2, 15, 10));
        prefsGrid.setBackground(PANEL_COLOR);
        
        // Notification preferences
        prefsGrid.add(new JLabel("Email Notifications:"));
        JCheckBox emailNotif = new JCheckBox("Enabled", true);
        prefsGrid.add(emailNotif);
        
        prefsGrid.add(new JLabel("Default View:"));
        JComboBox<String> defaultView = new JComboBox<>(new String[]{"List View", "Weekly View", "Monthly View"});
        prefsGrid.add(defaultView);
        
        prefsGrid.add(new JLabel("Time Format:"));
        JComboBox<String> timeFormat = new JComboBox<>(new String[]{"24-hour", "12-hour"});
        prefsGrid.add(timeFormat);
        
        JButton savePrefsBtn = new JButton("Save Preferences");
        styleButton(savePrefsBtn, ACCENT_COLOR);
        savePrefsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Preferences saved!");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(savePrefsBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(prefsGrid, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }
    
    private JLabel createInfoValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(Color.BLACK);
        return label;
    }
    
    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 36));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(iconLabel, BorderLayout.NORTH);
        centerPanel.add(titleLabel, BorderLayout.CENTER);
        centerPanel.add(valueLabel, BorderLayout.SOUTH);
        
        card.add(centerPanel, BorderLayout.CENTER);
        return card;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    // Helper methods for creating form fields
    private JTextField createFormField(String label, JPanel parent) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(jLabel);
        parent.add(Box.createVerticalStrut(5));
        
        JTextField field = new JTextField(20);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
        
        return field;
    }
    
    private JPasswordField createPasswordField(String label, JPanel parent) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(jLabel);
        parent.add(Box.createVerticalStrut(5));
        
        JPasswordField field = new JPasswordField(20);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));
        
        return field;
    }
    
    private JComboBox<String> createComboBoxField(String label, String[] items, JPanel parent) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(jLabel);
        parent.add(Box.createVerticalStrut(5));
        
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(combo);
        parent.add(Box.createVerticalStrut(15));
        
        return combo;
    }
    
    private void editProfile() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Edit Profile", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Using the helper methods
        JTextField firstNameField = createFormField("First Name:", formPanel);
        firstNameField.setText(currentUser.getFirstName());
        
        JTextField lastNameField = createFormField("Last Name:", formPanel);
        lastNameField.setText(currentUser.getLastName());
        
        JTextField emailField = createFormField("Email:", formPanel);
        emailField.setText(currentUser.getEmail());
        
        JTextField phoneField = createFormField("Phone:", formPanel);
        phoneField.setText(currentUser.getPhone());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Changes");
        JButton cancelBtn = new JButton("Cancel");
        
        styleButton(saveBtn, HEADER_COLOR);
        styleButton(cancelBtn, Color.GRAY);
        
        saveBtn.addActionListener(e -> {
            // Here you would update the user in the controller
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update user information (you need to implement this in your controller)
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
            dialog.dispose();
            
            // Refresh the profile panel
            initUI();
            revalidate();
            repaint();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}