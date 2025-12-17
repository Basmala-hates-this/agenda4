package com.agenda.vue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomePage extends JFrame {

    // Project theme colors
    private final Color THEME_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color BUTTON_COLOR = new Color(100, 180, 255);
    private final Color GOLD = new Color(218, 165, 32);
    private final Color DARK_BLUE = new Color(25, 25, 112);

    public WelcomePage() {
        setTitle("Collaborative Agenda - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));

        JLabel titleLabel = new JLabel("Collaborative Agenda");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Where planning your goals is uno click away");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(GOLD); // gold highlight
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Buttons panel (centered)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 20));

        JButton loginButton = createStyledButton("Login", BUTTON_COLOR, 150, 50);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.addActionListener(e -> openLoginDialog());

        JButton registerButton = createStyledButton("Sign Up", GOLD, 150, 50);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.addActionListener(e -> openRegisterDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(200, 225, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel footerLabel = new JLabel("Collaborative Agenda by -for crying out loud & i'm just a victim- 2025");
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
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

    private void openLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(null); // temporary null parent
        loginDialog.setVisible(true);

        if (loginDialog.isAuthenticated()) {
            dispose(); // close welcome page
            // Open main agenda here
        }
    }

    private void openRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog(null); // temporary null parent
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePage welcomePage = new WelcomePage();
            welcomePage.setVisible(true);
        });
    }
}
