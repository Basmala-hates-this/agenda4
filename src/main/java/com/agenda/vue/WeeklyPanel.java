package com.agenda.vue;

import com.agenda.controler.AgendaController;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeeklyPanel extends JPanel {
    private AgendaController controller;
    private LocalDate currentWeekStart;
    private WeeklyGridPanel gridPanel;
    private JLabel weekLabel;

    public WeeklyPanel(AgendaController controller, Runnable refreshCallback) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // Calculate Monday of current week
        currentWeekStart = LocalDate.now();
        while (currentWeekStart.getDayOfWeek() != DayOfWeek.MONDAY)
            currentWeekStart = currentWeekStart.minusDays(1);

        // Top navigation panel
        JPanel navPanel = new JPanel(new BorderLayout());
        weekLabel = new JLabel("", SwingConstants.CENTER);
        weekLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        navPanel.add(weekLabel, BorderLayout.CENTER);

        JButton prevButton = new JButton("<< Prev Week");
        prevButton.addActionListener(e -> changeWeek(-7));
        navPanel.add(prevButton, BorderLayout.WEST);

        JButton nextButton = new JButton("Next Week >>");
        nextButton.addActionListener(e -> changeWeek(7));
        navPanel.add(nextButton, BorderLayout.EAST);

        add(navPanel, BorderLayout.NORTH);

        // Grid panel
        gridPanel = new WeeklyGridPanel(controller, currentWeekStart, refreshCallback);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        updateWeekDisplay();
    }

    private void changeWeek(int days) {
        currentWeekStart = currentWeekStart.plusDays(days);
        updateWeekDisplay();
        gridPanel.updateWeek(currentWeekStart);
    }

    private void updateWeekDisplay() {
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        weekLabel.setText(currentWeekStart + " - " + weekEnd);
    }

    public void refreshView() {
        SwingUtilities.invokeLater(() -> gridPanel.repopulateGrid());
    }
}