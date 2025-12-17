package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private boolean authenticated = false;
    private User loggedUser;
    private AgendaController controller;

    // Theme colors
    private final Color THEME_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color BUTTON_COLOR = new Color(100, 180, 255);
    private final Color GOLD = new Color(218, 165, 32);

    public LoginDialog(Frame parent) {
        super(parent, "Login - Collaborative Agenda", true);
        this.controller = new AgendaController();

        setSize(450, 350);
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

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        emailField = createField("Email:", formPanel);
        passwordField = createPasswordField("Password:", formPanel);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BG_COLOR);

        loginButton = createStyledButton("Login", BUTTON_COLOR, 160, 50);
        loginButton.addActionListener(e -> login());

        registerButton = createStyledButton("Sign Up", GOLD, 160, 50);
        registerButton.addActionListener(e -> showRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);
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

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use AgendaController for authentication
        loggedUser = controller.authenticate(email, password);
        if (loggedUser != null) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(null);
        registerDialog.setVisible(true);
        if (registerDialog.isRegistered()) {
            emailField.setText(registerDialog.getRegisteredEmail());
            passwordField.requestFocus();
        }
    }

    public boolean isAuthenticated() { 
        return authenticated; 
    }
    
    public User getLoggedUser() { 
        return loggedUser; 
    }
}