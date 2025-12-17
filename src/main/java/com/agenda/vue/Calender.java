package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Calender extends JPanel {

    private AgendaController controller;
    private LocalDate currentMonth;
    private JPanel gridPanel;
    private JLabel monthLabel;
    private Runnable refreshCallback;

    public Calender(AgendaController controller, Runnable refreshCallback) {
        this.controller = controller;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout());

        currentMonth = LocalDate.now().withDayOfMonth(1);

        // Top panel with month label and navigation
        JPanel topPanel = new JPanel(new BorderLayout());

        // Month label
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        topPanel.add(monthLabel, BorderLayout.CENTER);

        // Navigation buttons
        JButton prevButton = new JButton("<< Prev Month");
        prevButton.addActionListener(e -> changeMonth(-1));
        topPanel.add(prevButton, BorderLayout.WEST);

        JButton nextButton = new JButton("Next Month >>");
        nextButton.addActionListener(e -> changeMonth(1));
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Grid panel
        gridPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        add(gridPanel, BorderLayout.CENTER);

        refreshView();
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        refreshView();
    }

    public void refreshView() {
        gridPanel.removeAll();

        // Month label
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());

        // Day headers - USING THE SAME BLUE AS WEEKLY VIEW
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            String day = days[i];
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setOpaque(true);
            
            // Darker blue for Friday (index 4) and Saturday (index 5) - weekend
            if (i == 4 || i == 5) { // Friday or Saturday
                label.setBackground(new Color(30, 80, 150)); // Darker blue for weekend
            } else {
                label.setBackground(new Color(70, 130, 180)); // Regular blue for weekdays
            }
            
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            gridPanel.add(label);
        }

        // Get today's date
        LocalDate today = LocalDate.now();
        
        // Blank cells for alignment
        int startDayOfWeek = currentMonth.getDayOfWeek().getValue();
        for (int i = 1; i < startDayOfWeek; i++) {
            gridPanel.add(new JLabel());
        }

        // Day cells
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.withDayOfMonth(day);
            JPanel cell = new JPanel(null);
            
            // Determine cell color based on day
            Color defaultBg;
            Color hoverBg;
            
            // Check if this is today's date
            boolean isToday = date.equals(today);
            
            // Check if this is Friday (5) or Saturday (6) - weekend
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 5=Friday, 6=Saturday
            
            if (isToday) {
                // Today gets faded gold
                defaultBg = new Color(255, 250, 205); // Faded Gold (Cornsilk) - same as zebra ODD_ROW_COLOR
                hoverBg = new Color(255, 245, 180);   // Slightly darker gold on hover
            } else if (dayOfWeek == 5 || dayOfWeek == 6) {
                // Friday or Saturday - weekend gets darker blue
                defaultBg = new Color(180, 210, 240); // Lighter version of dark blue header
                hoverBg = new Color(160, 190, 220);   // Darker on hover
            } else {
                // Regular weekday
                defaultBg = new Color(200, 220, 240); // light blue
                hoverBg = new Color(150, 190, 240);   // darker on hover
            }
            
            cell.setBackground(defaultBg);
            cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            
            // Set day label color based on day type
            if (isToday) {
                dayLabel.setForeground(new Color(184, 134, 11)); // Dark gold text for today
                dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14)); // Bolder for today
            } else if (dayOfWeek == 5 || dayOfWeek == 6) {
                dayLabel.setForeground(new Color(0, 51, 102)); // Dark blue text for weekend
                dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            } else {
                dayLabel.setForeground(new Color(0, 102, 204)); // Regular blue text
                dayLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            }
            
            dayLabel.setBounds(5, 5, 25, 25);
            cell.add(dayLabel);

            // Hover effect for entire cell
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { cell.setBackground(hoverBg); }
                @Override
                public void mouseExited(MouseEvent e) { cell.setBackground(defaultBg); }
            });

            // Events in this day
            List<Event> eventsInDay = controller.getEvents().stream()
                    .filter(ev -> ev.getDate().equals(date))
                    .collect(Collectors.toList());

            int yOffset = 30;
            for (Event event : eventsInDay) {
                JLabel eventLabel = new JLabel(event.getTitle());
                eventLabel.setToolTipText("<html>" + event.getDescription() +
                        "<br>Responsible: " + event.getResponsible() + "</html>");
                eventLabel.setBackground(Color.WHITE);
                eventLabel.setForeground(Color.DARK_GRAY);
                eventLabel.setOpaque(true);
                eventLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
                eventLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                eventLabel.setBounds(5, yOffset, 120, 20);
                cell.add(eventLabel);

                // Double-click edit
                eventLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            EventDialogUnified dialog = new EventDialogUnified(
                                    (Frame) SwingUtilities.getWindowAncestor(Calender.this),
                                    controller,
                                    EventDialogUnified.Mode.EDIT,
                                    event
                            );
                            dialog.setVisible(true);
                            refreshCallback.run();
                        }
                        // Right-click menu
                        if (SwingUtilities.isRightMouseButton(e)) {
                            MainFrame parentFrame = (MainFrame) SwingUtilities.getWindowAncestor(Calender.this);
                            JPopupMenu menu = parentFrame.createEventContextMenu(event);
                            menu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                });

                yOffset += 25;
            }

            // Click empty cell to add event
            cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                        EventDialogUnified dialog = new EventDialogUnified(
                                (Frame) SwingUtilities.getWindowAncestor(Calender.this),
                                controller,
                                EventDialogUnified.Mode.ADD,
                                null
                        );
                        dialog.setDate(date);
                        dialog.setVisible(true);
                        refreshCallback.run();
                    }
                }
            });

            gridPanel.add(cell);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}
