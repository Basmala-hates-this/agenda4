package com.agenda.vue;

import com.agenda.controler.EventController;
import com.agenda.model.Notification;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsPanel extends JPanel {
    private EventController eventController;
    private NotificationTableModel tableModel;
    private JTable notificationTable;
    private JButton markAllReadButton;
    private JButton refreshButton;
    
    // Colors
    private final Color BG_COLOR = new Color(220, 235, 255);
    private final Color HEADER_COLOR = new Color(70, 130, 180);
    private final Color UNREAD_COLOR = new Color(255, 250, 200);
    private final Color READ_COLOR = Color.WHITE;
    
    public NotificationsPanel(EventController eventController) {
        this.eventController = eventController;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        
        initUI();
        loadNotifications();
    }
    
    public NotificationsPanel() {
        // Default constructor for backward compatibility
        this(null);
    }
    
    private void initUI() {
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BG_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BG_COLOR);
        
        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(100, 180, 255));
        refreshButton.addActionListener(e -> loadNotifications());
        
        markAllReadButton = new JButton("Mark All as Read");
        styleButton(markAllReadButton, new Color(218, 165, 32));
        markAllReadButton.addActionListener(e -> markAllAsRead());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(markAllReadButton);
        
        titlePanel.add(buttonPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Notification table
        tableModel = new NotificationTableModel();
        notificationTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (!isRowSelected(row)) {
                    Notification notification = tableModel.getNotificationAt(row);
                    if (notification != null && !notification.isLu()) {
                        c.setBackground(UNREAD_COLOR);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setBackground(READ_COLOR);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        };
        
        notificationTable.setRowHeight(30);
        notificationTable.getTableHeader().setBackground(HEADER_COLOR);
        notificationTable.getTableHeader().setForeground(Color.WHITE);
        notificationTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Double-click to mark as read
        notificationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = notificationTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        markAsRead(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(notificationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(BG_COLOR);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel statusLabel = new JLabel("Double-click a notification to mark it as read");
        statusLabel.setForeground(Color.DARK_GRAY);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
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
    
    private void loadNotifications() {
        if (eventController != null) {
            List<Notification> notifications = eventController.getNotificationsForCurrentUser();
            tableModel.setNotifications(notifications);
        } else {
            // Show sample notifications for demo
            showSampleNotifications();
        }
        updateButtonStates();
    }
    
    private void showSampleNotifications() {
        // Sample notifications for demonstration
        java.util.List<Notification> sampleNotifications = new java.util.ArrayList<>();
        sampleNotifications.add(new Notification(1, 1, "Meeting with team at 2 PM", false, 
            java.time.LocalDateTime.now().minusHours(2)));
        sampleNotifications.add(new Notification(2, 1, "Project deadline tomorrow", false, 
            java.time.LocalDateTime.now().minusDays(1)));
        sampleNotifications.add(new Notification(3, 1, "Event 'Conference' has been updated", true, 
            java.time.LocalDateTime.now().minusDays(2)));
        sampleNotifications.add(new Notification(4, 1, "New event shared with you", true, 
            java.time.LocalDateTime.now().minusDays(3)));
        
        tableModel.setNotifications(sampleNotifications);
    }
    
    private void markAsRead(int row) {
        Notification notification = tableModel.getNotificationAt(row);
        if (notification != null && !notification.isLu()) {
            notification.setLu(true);
            tableModel.fireTableRowsUpdated(row, row);
            
            if (eventController != null) {
                eventController.markNotificationAsRead(notification.getNotif_id());
            }
            
            updateButtonStates();
        }
    }
    
    private void markAllAsRead() {
        if (eventController != null) {
            eventController.markAllNotificationsAsRead();
        }
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Notification notification = tableModel.getNotificationAt(i);
            if (notification != null) {
                notification.setLu(true);
            }
        }
        
        tableModel.fireTableDataChanged();
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        boolean hasUnread = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Notification notification = tableModel.getNotificationAt(i);
            if (notification != null && !notification.isLu()) {
                hasUnread = true;
                break;
            }
        }
        
        markAllReadButton.setEnabled(hasUnread);
    }
    
    public void refreshNotifications() {
        loadNotifications();
    }
    
    // Table model for notifications
    private class NotificationTableModel extends AbstractTableModel {
        private java.util.List<Notification> notifications = new java.util.ArrayList<>();
        private final String[] columns = {"Status", "Message", "Date"};
        
        @Override
        public int getRowCount() {
            return notifications.size();
        }
        
        @Override
        public int getColumnCount() {
            return columns.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columns[column];
        }
        
        @Override
        public Object getValueAt(int row, int column) {
            Notification notification = notifications.get(row);
            switch (column) {
                case 0:
                    return notification.isLu() ? "✓ Read" : "● Unread";
                case 1:
                    return notification.getMessage();
                case 2:
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    return notification.getDate().format(formatter);
                default:
                    return null;
            }
        }
        
        @Override
        public Class<?> getColumnClass(int column) {
            return String.class;
        }
        
        public void setNotifications(java.util.List<Notification> notifications) {
            this.notifications = notifications;
            fireTableDataChanged();
        }
        
        public Notification getNotificationAt(int row) {
            if (row >= 0 && row < notifications.size()) {
                return notifications.get(row);
            }
            return null;
        }
    }
}