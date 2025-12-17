package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;
import javax.swing.*;
import java.awt.*;

public class UserProfileApp extends JFrame {
    
    public UserProfileApp() {
        initUI();
    }
    
    private void initUI() {
        setTitle("User Profile - Collaborative Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Create a sample user (you can change this to your actual user)
        User sampleUser = createSampleUser();
        
        // Create AgendaController
        AgendaController controller = new AgendaController();
        
        // Create and add the UserProfilePanel
        UserProfilePanel userProfilePanel = new UserProfilePanel(controller, sampleUser);
        add(userProfilePanel);
        
        // Add menu bar
        createMenuBar();
        
        // Add status bar
        addStatusBar();
    }
    
    private User createSampleUser() {
        // Create a sample user for demonstration
        // In a real app, you would get this from your authentication system
        User user = new User();
        user.setId(2);
        user.setFirstName("bro");
        user.setLastName("tha");
        user.setEmail("mate@example.com");
        user.setPassword("user123");
        user.setPhone("123-456-7890");
        user.setRole("user");
        return user;
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JMenuItem backItem = new JMenuItem("Back to Agenda");
        backItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
        backItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Go back to main agenda?",
                "Confirm Navigation",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // You can launch your Main class here
                JOptionPane.showMessageDialog(this, 
                    "This would open the main agenda application.\n" +
                    "For now, please run Main.java separately.");
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(backItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Profile Menu
        JMenu profileMenu = new JMenu("Profile");
        profileMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JMenuItem refreshItem = new JMenuItem("Refresh Profile");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> JOptionPane.showMessageDialog(this, 
            "Profile refreshed!"));
        
        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.addActionListener(e -> showChangePasswordDialog());
        
        profileMenu.add(refreshItem);
        profileMenu.addSeparator();
        profileMenu.add(changePasswordItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JMenuItem zoomInItem = new JMenuItem("Zoom In");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke("ctrl PLUS"));
        
        JMenuItem zoomOutItem = new JMenuItem("Zoom Out");
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke("ctrl MINUS"));
        
        JMenuItem resetZoomItem = new JMenuItem("Reset Zoom");
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 0"));
        
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        viewMenu.add(resetZoomItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JMenuItem helpItem = new JMenuItem("User Guide");
        helpItem.setAccelerator(KeyStroke.getKeyStroke("F6"));
        helpItem.addActionListener(e -> showHelp());
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(profileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void addStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(200, 225, 245));
        
        JLabel statusLabel = new JLabel("Ready - User Profile");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JLabel userLabel = new JLabel("Logged in as: bruh");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(70, 130, 180));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(userLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void showChangePasswordDialog() {
        JDialog passwordDialog = new JDialog(this, "Change Password", true);
        passwordDialog.setSize(400, 250);
        passwordDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        JLabel currentPassLabel = new JLabel("Current Password:");
        currentPassLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        currentPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(currentPassLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        JPasswordField currentPassField = new JPasswordField(20);
        currentPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        currentPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(currentPassField);
        formPanel.add(Box.createVerticalStrut(15));
        
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        newPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(newPassLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        JPasswordField newPassField = new JPasswordField(20);
        newPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        newPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(newPassField);
        formPanel.add(Box.createVerticalStrut(15));
        
        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
        confirmPassLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmPassLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(confirmPassLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        JPasswordField confirmPassField = new JPasswordField(20);
        confirmPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        confirmPassField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(confirmPassField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton changeBtn = new JButton("Change Password");
        JButton cancelBtn = new JButton("Cancel");
        
        changeBtn.setBackground(new Color(70, 130, 180));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        changeBtn.addActionListener(e -> {
            char[] newPass = newPassField.getPassword();
            char[] confirmPass = confirmPassField.getPassword();
            
            if (newPass.length < 6) {
                JOptionPane.showMessageDialog(passwordDialog,
                    "Password must be at least 6 characters long!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!java.util.Arrays.equals(newPass, confirmPass)) {
                JOptionPane.showMessageDialog(passwordDialog,
                    "New passwords do not match!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(passwordDialog,
                "Password changed successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            passwordDialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> passwordDialog.dispose());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(changeBtn);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        passwordDialog.add(panel);
        passwordDialog.setVisible(true);
    }
    
    private void showHelp() {
        String helpText = "<html><div style='font-size:12px; padding:10px;'>" +
                "<h2>User Profile Help</h2>" +
                "<p><b>Personal Information:</b> View and edit your personal details.</p>" +
                "<p><b>Statistics:</b> See your activity statistics (events created, shared, etc.).</p>" +
                "<p><b>Preferences:</b> Customize your application settings.</p>" +
                "<br>" +
                "<h3>Keyboard Shortcuts:</h3>" +
                "<ul>" +
                "<li><b>Ctrl+B:</b> Back to Main Agenda</li>" +
                "<li><b>Ctrl+Q:</b> Exit Application</li>" +
                "<li><b>F1:</b> Show Help</li>" +
                "<li><b>F5:</b> Refresh Profile</li>" +
                "</ul>" +
                "</div></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "User Profile Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAbout() {
        String aboutText = "<html><div style='font-size:12px; padding:10px; text-align:center;'>" +
                "<h2>Collaborative Agenda</h2>" +
                "<h3>User Profile Module</h3>" +
                "<p>Version 1.0</p>" +
                "<p>© 2025 Collaborative Agenda System</p>" +
                "<p>Manage your personal information, preferences, and activity statistics.</p>" +
                "<hr>" +
                "<p style='font-size:10px; color:gray;'>" +
                "This is a standalone demo of the User Profile interface.<br>" +
                "In the full application, this integrates with the main agenda system." +
                "</p>" +
                "</div></html>";
        
        JOptionPane.showMessageDialog(this, aboutText, "About User Profile", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety
        SwingUtilities.invokeLater(() -> {
            UserProfileApp app = new UserProfileApp();
            app.setVisible(true);
            
            // Optional: Show welcome message
            JOptionPane.showMessageDialog(app,
                "Welcome to the User Profile Demo!\n\n" +
                "This shows the standalone user profile interface.\n" +
                "In the full application, this would be integrated with the main agenda.",
                "User Profile Demo",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}