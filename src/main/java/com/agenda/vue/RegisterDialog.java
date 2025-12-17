package com.agenda.vue;

import com.agenda.controler.AgendaController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterDialog extends JDialog {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JButton registerButton;
    private JButton cancelButton;
    private JButton backToLoginButton;

    private boolean registered = false;
    private String registeredEmail;
    private AgendaController controller;

    // Theme colors
    private final Color THEME_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color BUTTON_COLOR = new Color(100, 180, 255);
    private final Color GOLD = new Color(218, 165, 32);

    public RegisterDialog(Frame parent) {
        super(parent, "Sign Up - Collaborative Agenda", true);
        this.controller = new AgendaController();

        setSize(500, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(THEME_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        firstNameField = createField("First Name:", formPanel);
        lastNameField = createField("Last Name:", formPanel);
        emailField = createField("Email:", formPanel);
        phoneField = createField("Phone Number:", formPanel);

        // Role ComboBox
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleLabel);
        formPanel.add(Box.createVerticalStrut(5));

        roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createVerticalStrut(15));

        passwordField = createPasswordField("Password:", formPanel);
        confirmPasswordField = createPasswordField("Confirm Password:", formPanel);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Buttons panel - right aligned
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(BG_COLOR);

        registerButton = createStyledButton("Sign Up", BUTTON_COLOR, 140, 40);
        registerButton.addActionListener(e -> register());

        cancelButton = createStyledButton("Cancel", GOLD, 140, 40);
        cancelButton.addActionListener(e -> dispose());

        backToLoginButton = createStyledButton(" Login", THEME_COLOR, 140, 40);
        backToLoginButton.addActionListener(e -> {
            dispose();
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);
        });

        buttonPanel.add(backToLoginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(registerButton);
    }

    private JTextField createField(String labelText, JPanel parent) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));

        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));

        return field;
    }

    private JPasswordField createPasswordField(String labelText, JPanel parent) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(label);
        parent.add(Box.createVerticalStrut(5));

        JPasswordField field = new JPasswordField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(field);
        parent.add(Box.createVerticalStrut(15));

        return field;
    }

    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setPreferredSize(new Dimension(width, height));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setCursor(Cursor.getDefaultCursor());
            }
        });

        return button;
    }

    private void register() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleComboBox.getSelectedItem().toString();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use AgendaController to check if email exists
        if (controller.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "This email is already in use", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use AgendaController to register user
        boolean success = controller.register(firstName, lastName, email, phone, role, password);
        if (success) {
            registered = true;
            registeredEmail = email;
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistered() { 
        return registered; 
    }
    
    public String getRegisteredEmail() { 
        return registeredEmail; 
    }
}