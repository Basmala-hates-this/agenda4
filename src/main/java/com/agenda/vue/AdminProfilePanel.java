package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminProfilePanel extends JPanel {
    private AgendaController controller;
    private User currentUser;
    
    // Colors
    private final Color HEADER_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color GOLD = new Color(218, 165, 32);
    
    public AdminProfilePanel(AgendaController controller, User currentUser) {
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
        
        // Admin Controls
        JPanel controlsPanel = createAdminControlsPanel();
        mainPanel.add(controlsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // User Management
        JPanel userManagementPanel = createUserManagementPanel();
        mainPanel.add(userManagementPanel);
        
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
        
        JLabel titleLabel = new JLabel("Admin Profile");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(HEADER_COLOR);
        
        JLabel userLabel = new JLabel("Logged in as: " + currentUser.getNom());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(Color.DARK_GRAY);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(PANEL_COLOR);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(userLabel);
        
        JLabel adminBadge = new JLabel("⚙️ ADMINISTRATOR");
        adminBadge.setFont(new Font("SansSerif", Font.BOLD, 14));
        adminBadge.setForeground(GOLD);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(adminBadge, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createAdminControlsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(BG_COLOR);
        
        // Statistics Card
        JPanel statsCard = createCard("📊 Statistics", new Color(100, 180, 255));
        JPanel statsContent = new JPanel(new GridLayout(2, 2, 10, 10));
        statsContent.setBackground(Color.WHITE);
        
        statsContent.add(createStatLabel("Total Users", "15"));
        statsContent.add(createStatLabel("Active Events", "42"));
        statsContent.add(createStatLabel("Today's Events", "7"));
        statsContent.add(createStatLabel("System Status", "✓ Online"));
        
        statsCard.add(statsContent, BorderLayout.CENTER);
        
        // Quick Actions Card
        JPanel actionsCard = createCard("⚡ Quick Actions", new Color(218, 165, 32));
        JPanel actionsContent = new JPanel();
        actionsContent.setLayout(new BoxLayout(actionsContent, BoxLayout.Y_AXIS));
        actionsContent.setBackground(Color.WHITE);
        
        JButton backupBtn = createActionButton("💾 Backup Data", HEADER_COLOR);
        JButton logsBtn = createActionButton("📋 View Logs", HEADER_COLOR);
        JButton settingsBtn = createActionButton("⚙️ System Settings", HEADER_COLOR);
        
        backupBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Backup feature coming soon!"));
        logsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logs viewer coming soon!"));
        settingsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "System settings coming soon!"));
        
        actionsContent.add(backupBtn);
        actionsContent.add(Box.createVerticalStrut(10));
        actionsContent.add(logsBtn);
        actionsContent.add(Box.createVerticalStrut(10));
        actionsContent.add(settingsBtn);
        
        actionsCard.add(actionsContent, BorderLayout.CENTER);
        
        panel.add(statsCard);
        panel.add(actionsCard);
        
        return panel;
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, HEADER_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel title = new JLabel(" User Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(HEADER_COLOR);
        
        JButton addUserBtn = new JButton("+ Add User");
        styleButton(addUserBtn, GOLD);
        addUserBtn.addActionListener(e -> addNewUser());
        
        header.add(title, BorderLayout.WEST);
        header.add(addUserBtn, BorderLayout.EAST);
        
        // User Table (simplified for now)
        String[] columns = {"ID", "Name", "Email", "Role", "Actions"};
        Object[][] data = {
            {1, "Admin User", "admin@agenda.com", "Admin", "Edit/Delete"},
            {2, "Regular User", "user@agenda.com", "User", "Edit/Delete"},
            {3, "John Doe", "john@example.com", "User", "Edit/Delete"}
        };
        
        JTable userTable = new JTable(data, columns);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setBackground(HEADER_COLOR);
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JScrollPane tableScroll = new JScrollPane(userTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCard(String title, Color titleColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(titleColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }
    
    private JLabel createStatLabel(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelLbl.setForeground(Color.DARK_GRAY);
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLbl.setForeground(HEADER_COLOR);
        
        panel.add(labelLbl, BorderLayout.NORTH);
        panel.add(valueLbl, BorderLayout.SOUTH);
        
        // Wrap in JLabel to return
        JLabel container = new JLabel();
        container.setLayout(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        return container;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
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
        
        return button;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
    
    private void addNewUser() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        JTextField firstNameField = createFormField("First Name:", formPanel);
        JTextField lastNameField = createFormField("Last Name:", formPanel);
        JTextField emailField = createFormField("Email:", formPanel);
        JPasswordField passwordField = createPasswordField("Password:", formPanel);
        JComboBox<String> roleCombo = createComboBoxField("Role:", new String[]{"User", "Admin"}, formPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        styleButton(saveBtn, HEADER_COLOR);
        styleButton(cancelBtn, Color.GRAY);
        
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "User added successfully!");
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
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
}