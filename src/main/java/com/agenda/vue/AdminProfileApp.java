

package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class AdminProfileApp extends JFrame {

    private AgendaController controller;
    private User currentUser;

    // ===== COLORS =====
    private final Color HEADER_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color PANEL_COLOR = Color.WHITE;
    private final Color GOLD = new Color(218, 165, 32);

    public AdminProfileApp() {
        controller = new AgendaController();
        currentUser = createAdminUser();
        initFrame();
        initUI();
    }

    // ================= FRAME =================
    private void initFrame() {
        setTitle("Admin Profile - Collaborative Agenda");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(createAppIcon());
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
        mainPanel.add(createAdminControlsPanel());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createUserManagementPanel());

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

        JLabel title = new JLabel("Admin Profile");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(HEADER_COLOR);

        JLabel user = new JLabel("Logged in as: " + currentUser.getFirstName());

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(PANEL_COLOR);
        left.add(title);
        left.add(user);

        JLabel badge = new JLabel("⚙️ ADMINISTRATOR");
        badge.setForeground(GOLD);
        badge.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(left, BorderLayout.WEST);
        panel.add(badge, BorderLayout.EAST);
        return panel;
    }

    // ================= CONTROLS =================
    private JPanel createAdminControlsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setBackground(BG_COLOR);

        panel.add(createStatsCard());
        panel.add(createActionsCard());

        return panel;
    }

    private JPanel createStatsCard() {
        JPanel card = createCard("📊 Statistics", HEADER_COLOR);
        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setBackground(Color.WHITE);

        grid.add(createStat("Total Users", "15"));
        grid.add(createStat("Active Events", "42"));
        grid.add(createStat("Today's Events", "7"));
        grid.add(createStat("System", "✓ Online"));

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionsCard() {
        JPanel card = createCard("⚡ Quick Actions", GOLD);
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);

        box.add(actionButton("💾 Backup", () -> performBackup()));
        box.add(Box.createVerticalStrut(10));
        box.add(actionButton("📋 Logs", () -> showSystemLogs()));
        box.add(Box.createVerticalStrut(10));
        box.add(actionButton("⚙️ Settings", () -> showSystemSettings()));

        card.add(box, BorderLayout.CENTER);
        return card;
    }

    // ================= USERS =================
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(HEADER_COLOR);

        JButton add = new JButton("+ Add User");
        styleButton(add, GOLD);
        add.addActionListener(e -> addNewUser());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_COLOR);
        header.add(title, BorderLayout.WEST);
        header.add(add, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Email", "Role"};
        Object[][] data = {
                {1, "Admin", "admin@agenda.com", "Admin"},
                {2, "User", "user@agenda.com", "User"}
        };

        JTable table = new JTable(data, cols);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(HEADER_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ================= HELPERS =================
    private JPanel createCard(String title, Color c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setForeground(c);
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    private JPanel createStat(String k, String v) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel l1 = new JLabel(k);
        JLabel l2 = new JLabel(v);
        l2.setFont(new Font("SansSerif", Font.BOLD, 18));
        l2.setForeground(HEADER_COLOR);
        p.add(l1, BorderLayout.NORTH);
        p.add(l2, BorderLayout.SOUTH);
        return p;
    }

    private JButton actionButton(String text, Runnable r) {
        JButton b = new JButton(text);
        styleButton(b, HEADER_COLOR);
        b.addActionListener(e -> r.run());
        return b;
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    // ================= ADMIN ACTIONS =================
    private void addNewUser() {
        JOptionPane.showMessageDialog(this, "Add User dialog");
    }

    private void showSystemLogs() {
        JOptionPane.showMessageDialog(this, "System Logs");
    }

    private void performBackup() {
        JOptionPane.showMessageDialog(this, "Backup running...");
    }

    private void showSystemSettings() {
        JOptionPane.showMessageDialog(this, "System Settings");
    }

    // ================= MENU + STATUS =================
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
        JLabel status = new JLabel("Administrator Mode - Ready");
        status.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(status, BorderLayout.SOUTH);
    }

    // ================= DATA =================
    private User createAdminUser() {
        User u = new User();
        u.setFirstName("Admin");
        u.setRole("admin");
        return u;
    }

    private Image createAppIcon() {
        return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminProfileApp().setVisible(true));
    }
}
