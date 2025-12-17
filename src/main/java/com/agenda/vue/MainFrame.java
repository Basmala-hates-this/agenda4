package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;
import com.agenda.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainFrame extends JFrame {

    private final AgendaController controller;
    private User currentUser;

    // View Panels
    private WeeklyPanel weeklyPanel;
    private EventListPanel listPanel;
    private Calender calPanel;

    // Layout components
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Navigation Buttons and State Management
    private JButton btnList;
    private JButton btnWeekly;
    private JButton btnMonthly;
    private JButton currentlySelectedButton;

    // Status bar
    private JLabel statusBar;

    // Notification & alert system
    private final Timer alertTimer;
    private final int ALERT_MINUTES = 60;
    private final Set<Event> alertedEvents = new HashSet<>();

    // Design Colors
    private final Color THEME_COLOR = new Color(70, 130, 180);
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color FOOTER_COLOR = new Color(200, 225, 245);
    private final Color GOLD = new Color(218, 165, 32);
    private final Color DARK_BLUE = new Color(25, 25, 112);

    // Toast management
    private final List<JDialog> activeToasts = new ArrayList<>();

    public MainFrame(AgendaController controller) {
        this.controller = controller;

        // Load current user from JSON or create default admin
        List<User> allUsers = controller.getUsers();
        if (allUsers.isEmpty()) {
            currentUser = new User(1, "Admin", "Adminson", "admin@agenda.com", "admin", "0000000000", "admin");
            controller.addUser(currentUser);
        } else {
            currentUser = allUsers.get(0);
        }

        setTitle("Collaborative Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);

        initUI();
        setupShortcuts();

        alertTimer = new Timer(3 * 1000, e -> checkUpcomingEvents());
        alertTimer.setInitialDelay(0);
        alertTimer.start();

        // Start with list view
        switchView("LIST");
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- 1. MENU BAR ---
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 245, 255));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("SansSerif", Font.BOLD, 14));

        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        helpItem.addActionListener(e -> showHelpDialog());

        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        closeItem.addActionListener(e -> closeApplication());

        fileMenu.add(helpItem);
        fileMenu.addSeparator();
        fileMenu.add(closeItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // --- 2. HEADER SECTION ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("Collaborative Agenda");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(25));

        // Navigation Buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navPanel.setBackground(BG_COLOR);

        btnList = createNavButton("List", "LIST");
        btnWeekly = createNavButton("Weekly", "WEEKLY");
        btnMonthly = createNavButton("Monthly", "MONTHLY");

        navPanel.add(btnList);
        navPanel.add(btnWeekly);
        navPanel.add(btnMonthly);

        headerPanel.add(navPanel);
        headerPanel.add(Box.createVerticalStrut(15));

        add(headerPanel, BorderLayout.NORTH);

        // --- 3. CENTER CONTENT ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
        mainContentPanel.setBackground(BG_COLOR);

        weeklyPanel = new WeeklyPanel(controller, this::refreshAllViews);
        calPanel = new Calender(controller, this::refreshAllViews);
        listPanel = new EventListPanel(controller, this::refreshAllViews);

        mainContentPanel.add(listPanel, "LIST");
        mainContentPanel.add(weeklyPanel, "WEEKLY");
        mainContentPanel.add(calPanel, "MONTHLY");

        add(mainContentPanel, BorderLayout.CENTER);

        // --- 4. FOOTER SECTION ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(FOOTER_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        statusBar = new JLabel("Ready");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        footerPanel.add(statusBar, BorderLayout.WEST);

        JButton addEventButton = createAddEventButton();
        footerPanel.add(addEventButton, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createAddEventButton() {
        JButton button = new JButton("＋ Add Event");
        button.setBackground(THEME_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 100, 160), 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(50, 110, 170));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(THEME_COLOR);
            }
        });

        button.addActionListener(e -> openAddEventDialog());
        return button;
    }

    private void showHelpDialog() {
        String helpText = "<html><div style='font-size:12px; padding:10px;'>" +
                "<h2>Keyboard Shortcuts</h2>" +
                "<table border='0' cellpadding='5'>" +
                "<tr><td><b>Ctrl + N</b></td><td>Add new event</td></tr>" +
                "<tr><td><b>Ctrl + Q</b></td><td>Close application</td></tr>" +
                "<tr><td><b>Ctrl + L</b></td><td>List view</td></tr>" +
                "<tr><td><b>Ctrl + W</b></td><td>Weekly view</td></tr>" +
                "<tr><td><b>Ctrl + M</b></td><td>Monthly view</td></tr>" +
                "<tr><td><b>Delete</b></td><td>Delete event</td></tr>" +
                "<tr><td><b>Ctrl + H</b></td><td>Show help</td></tr>" +
                "</table></div></html>";

        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void closeApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to close?",
                "Confirm Close",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private JButton createNavButton(String text, String viewName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentlySelectedButton) btn.setBackground(GOLD);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentlySelectedButton) btn.setBackground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> switchView(viewName));
        return btn;
    }

    private void updateNavButtons(JButton selectedButton) {
        if (currentlySelectedButton != null) {
            currentlySelectedButton.setBackground(Color.WHITE);
            currentlySelectedButton.setForeground(Color.BLACK);
        }

        selectedButton.setBackground(DARK_BLUE);
        selectedButton.setForeground(Color.WHITE);
        currentlySelectedButton = selectedButton;
    }

    public void switchView(String viewName) {
        JButton buttonToSelect = null;
        switch (viewName) {
            case "LIST" -> buttonToSelect = btnList;
            case "WEEKLY" -> buttonToSelect = btnWeekly;
            case "MONTHLY" -> buttonToSelect = btnMonthly;
        }

        if (buttonToSelect != null) updateNavButtons(buttonToSelect);
        cardLayout.show(mainContentPanel, viewName);

        if (statusBar != null) setStatus("Switched to " + viewName + " view");
    }

    public void notifyEventCreated() {
        setStatus("Event created");
        notifyToast("Event created!");
    }

    public void notifyEventModified() {
        setStatus("Event modified");
        notifyToast("Event updated!");
    }

    public void notifyEventDeleted() {
        setStatus("Event deleted");
        notifyToast("Event deleted!");
    }

    public void notifyActionCanceled(String action) {
        setStatus(action + " canceled");
    }

    public void notifyShare(int userCount) {
        if (userCount > 0) {
            setStatus("Event shared with " + userCount + " user(s)");
            notifyToast("Shared with " + userCount + " users");
        } else {
            setStatus("Share canceled");
        }
    }

    public void refreshAllViews() {
        SwingUtilities.invokeLater(() -> {
            if (weeklyPanel != null) weeklyPanel.refreshView();
            if (calPanel != null) calPanel.refreshView();
            if (listPanel != null) listPanel.refreshList();
            alertedEvents.removeIf(ev -> !controller.getEvents().contains(ev));
        });
    }

    public void setStatus(String text) {
        if (statusBar != null) statusBar.setText(text);
    }

    public void notifyToast(String message) {
        setStatus(message);

        JDialog toastDialog = new JDialog(this, false);
        toastDialog.setUndecorated(true);

        JPanel toastPanel = new JPanel(new BorderLayout());
        toastPanel.setBackground(THEME_COLOR);
        toastPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 100, 160), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel toastLabel = new JLabel(message, SwingConstants.CENTER);
        toastLabel.setForeground(Color.WHITE);
        toastLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        toastPanel.add(toastLabel, BorderLayout.CENTER);

        toastDialog.setContentPane(toastPanel);
        toastDialog.pack();

        Point loc = getLocation();
        Dimension size = getSize();
        toastDialog.setLocation(
                loc.x + (size.width - toastDialog.getWidth()) / 2,
                loc.y + size.height - 100
        );

        toastDialog.setVisible(true);

        Timer timer = new Timer(2500, e -> {
            toastDialog.dispose();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void openAddEventDialog() {
        setStatus("Creating new event");
        EventDialogUnified dialog = new EventDialogUnified(this, controller, EventDialogUnified.Mode.ADD, null);
        dialog.setVisible(true);
        refreshAllViews();
    }

    private void checkUpcomingEvents() {
        List<Event> events = controller.getEvents();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (Event ev : events) {
            if (!ev.getDate().equals(today)) continue;

            try {
                String[] parts = ev.getTime().split(":");
                int evHour = Integer.parseInt(parts[0]);
                int evMin = Integer.parseInt(parts[1]);
                LocalTime eventTime = LocalTime.of(evHour, evMin);
                LocalTime alertTime = eventTime.minusMinutes(ALERT_MINUTES);

                if (!alertedEvents.contains(ev) && !now.isBefore(alertTime) && now.isBefore(eventTime)) {
                    long minutesRemaining = ChronoUnit.MINUTES.between(now, eventTime);

                    if (minutesRemaining >= 0) {
                        alertedEvents.add(ev);
                        String message = String.format("Event \"%s\" starts in %d minutes!", ev.getTitle(), minutesRemaining);
                        notifyToast(message);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    public List<String> showUnifiedShareDialog(Component parent, String title) {
        String[] shareUsers = {
                "User1", "User2", "User3", "User4", "User5",
                "User6", "User7", "User8", "User9", "User10"
        };

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String user : shareUsers) {
            JCheckBox cb = new JCheckBox(user);
            checkBoxes.add(cb);
            panel.add(cb);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectAll = new JButton("Select All");
        JButton deselectAll = new JButton("Deselect All");
        buttonPanel.add(selectAll);
        buttonPanel.add(deselectAll);

        selectAll.addActionListener(ae -> checkBoxes.forEach(cb -> cb.setSelected(true)));
        deselectAll.addActionListener(ae -> checkBoxes.forEach(cb -> cb.setSelected(false)));

        JPanel container = new JPanel(new BorderLayout());
        container.add(new JScrollPane(panel), BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        container.setPreferredSize(new Dimension(300, 400));

        int result = JOptionPane.showConfirmDialog(parent, container, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            List<String> selected = new ArrayList<>();
            for (JCheckBox cb : checkBoxes) if (cb.isSelected()) selected.add(cb.getText());
            return selected;
        }
        return null;
    }

    public JPopupMenu createEventContextMenu(Event event) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> {
            setStatus("Editing event: " + event.getTitle());
            EventDialogUnified dialog = new EventDialogUnified(this, controller, EventDialogUnified.Mode.EDIT, event);
            dialog.setVisible(true);
            refreshAllViews();
        });
        menu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Delete this event?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                controller.deleteEvent(event);
                refreshAllViews();
                notifyEventDeleted();
            } else notifyActionCanceled("Deletion");
        });
        menu.add(deleteItem);

        JMenuItem shareItem = new JMenuItem("Share");
        shareItem.addActionListener(e -> {
            List<String> selectedUsers = showUnifiedShareDialog(this, "Share Event: " + event.getTitle());
            notifyShare(selectedUsers != null ? selectedUsers.size() : 0);
        });
        menu.add(shareItem);

        return menu;
    }

    private void setupShortcuts() {
        JRootPane root = getRootPane();

        KeyStroke addKey = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(addKey, "addEvent");
        root.getActionMap().put("addEvent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddEventDialog();
            }
        });

        KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delKey, "deleteEvent");
        root.getActionMap().put("deleteEvent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listPanel != null && listPanel.isVisible()) {
                    Event selected = listPanel.getSelectedEvent();
                    if (selected == null) {
                        setStatus("No event selected.");
                        return;
                    }
                    int result = JOptionPane.showConfirmDialog(MainFrame.this,
                            "Delete this event?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        controller.deleteEvent(selected);
                        refreshAllViews();
                        notifyEventDeleted();
                    } else notifyActionCanceled("Deletion");
                }
            }
        });

        KeyStroke closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKey, "closeApp");
        root.getActionMap().put("closeApp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApplication();
            }
        });

        KeyStroke listKey = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(listKey, "goToList");
        root.getActionMap().put("goToList", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("LIST");
            }
        });

        KeyStroke weeklyKey = KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(weeklyKey, "goToWeekly");
        root.getActionMap().put("goToWeekly", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("WEEKLY");
            }
        });

        KeyStroke monthlyKey = KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(monthlyKey, "goToMonthly");
        root.getActionMap().put("goToMonthly", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("MONTHLY");
            }
        });

        KeyStroke helpKey = KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(helpKey, "showHelp");
        root.getActionMap().put("showHelp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelpDialog();
            }
        });
    }
}
