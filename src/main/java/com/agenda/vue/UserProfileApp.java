// Merged version of UserProfileApp + UserProfilePanel
// Single JFrame class, UI + logic together
// NO UI, menu, status bar, colors, or dialogs removed

package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;

import javax.swing.*;
import java.awt.*;


public class UserProfileApp extends JFrame {

    private AgendaController controller;
    private User currentUser;

    // ===== COLORS =====
    private final Color HEADER_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(100, 180, 255);

    public UserProfileApp() {
        controller = new AgendaController();
        currentUser = createSampleUser();
        initFrame();
        initUI();
    }

    // ================= FRAME =================
    private void initFrame() {
        setTitle("User Profile - Collaborative Agenda");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        createMenuBar();
        addStatusBar();
    }

    // ================= UI =================
    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeaderPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createUserInfoPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createUserStatsPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createPreferencesPanel());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    // ================= HEADER =================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(HEADER_COLOR);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getFirstName() + "!");

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(PANEL_COLOR);
        left.add(title);
        left.add(welcome);

        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 48));

        panel.add(left, BorderLayout.WEST);
        panel.add(avatar, BorderLayout.EAST);
        return panel;
    }

    // ================= INFO =================
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Personal Information");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(HEADER_COLOR);

        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 10));
        grid.setBackground(PANEL_COLOR);

        grid.add(infoLabel("Full Name:"));
        grid.add(infoValue(currentUser.getFirstName() + " " + currentUser.getLastName()));
        grid.add(infoLabel("Email:"));
        grid.add(infoValue(currentUser.getEmail()));
        grid.add(infoLabel("Phone:"));
        grid.add(infoValue(currentUser.getPhone()));
        grid.add(infoLabel("Account Type:"));
        grid.add(infoValue(currentUser.getRole()));

        JButton edit = new JButton("Edit Profile");
        styleButton(edit, ACCENT_COLOR);
        edit.addActionListener(e -> editProfile());

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.setBackground(PANEL_COLOR);
        btn.add(edit);

        panel.add(title, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);
        return panel;
    }

    // ================= STATS =================
    private JPanel createUserStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBackground(BG_COLOR);

        panel.add(statCard("📅", "Events Created", "24", HEADER_COLOR));
        panel.add(statCard("👥", "Shared Events", "12", new Color(218, 165, 32)));
        panel.add(statCard("🔔", "Notifications", "8", ACCENT_COLOR));
        return panel;
    }

    // ================= PREFS =================
    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel title = new JLabel("Preferences");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(HEADER_COLOR);

        JPanel grid = new JPanel(new GridLayout(3, 2, 15, 10));
        grid.setBackground(PANEL_COLOR);

        grid.add(new JLabel("Email Notifications:"));
        grid.add(new JCheckBox("Enabled", true));
        grid.add(new JLabel("Default View:"));
        grid.add(new JComboBox<>(new String[]{"List View", "Weekly View", "Monthly View"}));
        grid.add(new JLabel("Time Format:"));
        grid.add(new JComboBox<>(new String[]{"24-hour", "12-hour"}));

        JButton save = new JButton("Save Preferences");
        styleButton(save, ACCENT_COLOR);
        save.addActionListener(e -> JOptionPane.showMessageDialog(this, "Preferences saved!"));

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn.setBackground(PANEL_COLOR);
        btn.add(save);

        panel.add(title, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        panel.add(btn, BorderLayout.SOUTH);
        return panel;
    }

    // ================= MENU =================
    private void createMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);
        bar.add(file);
        setJMenuBar(bar);
    }

    private void addStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(new Color(200, 225, 245));
        status.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        status.add(new JLabel("Ready - User Profile"), BorderLayout.WEST);
        status.add(new JLabel("Logged in as: " + currentUser.getFirstName()), BorderLayout.EAST);
        add(status, BorderLayout.SOUTH);
    }

    // ================= HELPERS =================
    private JLabel infoLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }

    private JLabel infoValue(String t) {
        JLabel l = new JLabel(t);
        return l;
    }

    private JPanel statCard(String icon, String title, String value, Color c) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(c, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel ic = new JLabel(icon, SwingConstants.CENTER);
        ic.setFont(new Font("SansSerif", Font.PLAIN, 36));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("SansSerif", Font.BOLD, 24));
        v.setForeground(c);

        card.add(ic, BorderLayout.NORTH);
        card.add(t, BorderLayout.CENTER);
        card.add(v, BorderLayout.SOUTH);
        return card;
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    // ================= EDIT =================
    private void editProfile() {
        JOptionPane.showMessageDialog(this, "Edit Profile dialog");
    }

    // ================= DATA =================
    private User createSampleUser() {
        User u = new User();
        u.setFirstName("bro");
        u.setLastName("tha");
        u.setEmail("mate@example.com");
        u.setPhone("123-456-7890");
        u.setRole("user");
        return u;
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserProfileApp().setVisible(true));
    }
}
