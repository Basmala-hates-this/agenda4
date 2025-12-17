package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;
import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class EventDetailsDialog extends JDialog {
    private  JTextField titleField;
    private JComboBox<String> responsibleCombo; // Changed from JTextField
    private JComboBox<String> hourCombo; 
    private JTextArea descriptionArea; // ADDED
    private JButton modifyButton, deleteButton, closeButton;
    private AgendaController controller;
    private Event originalEvent;
    
    private final String[] participants = {"Select Responsible", "Alice", "Bob", "Charlie", "David","Jon","Sarah","Noah","Le Me","Why","Cant","I Think","Of Names","Fuck","This","Shit"};

    public EventDetailsDialog(Frame owner, boolean modal, AgendaController controller, Event event) {
        super(owner, modal);
        this.controller = controller;
        this.originalEvent = event;

        setTitle("Event Details (" + event.getDate() + ")");
        setSize(450, 450); // Increased size
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 248, 255)); 

        // Font settings
        Font labelFont = new Font("SansSerif", Font.BOLD, 13);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(200, 32);

        // --- Top Fields Panel (Grid Layout) ---
        JPanel topFields = new JPanel(new GridLayout(4, 2, 10, 10));
        topFields.setBackground(getContentPane().getBackground());

        // Title
        JLabel titleLabel = new JLabel("Title :");
        titleLabel.setFont(labelFont);
        topFields.add(titleLabel);
        titleField = new JTextField(event.getTitle());
        titleField.setFont(fieldFont);
        titleField.setPreferredSize(fieldSize);
        topFields.add(titleField);
        
        // Responsible - JComboBox
        JLabel respLabel = new JLabel("Responsible :");
        respLabel.setFont(labelFont);
        topFields.add(respLabel);
        responsibleCombo = new JComboBox<>(participants);
        responsibleCombo.setFont(fieldFont);
        responsibleCombo.setPreferredSize(fieldSize);
        responsibleCombo.setSelectedItem(event.getResponsible());
        topFields.add(responsibleCombo);

        // Hour (ComboBox for hours 07:00 to 18:00)
        String[] hours = IntStream.rangeClosed(7, 18) // Hours 7 through 18
                                  .mapToObj(h -> String.format("%02d:00", h))
                                  .toArray(String[]::new);
        
        hourCombo = new JComboBox<>(hours);
        hourCombo.setFont(fieldFont);
        hourCombo.setPreferredSize(fieldSize);
        hourCombo.setSelectedItem(event.getTime());
        JLabel timeLabel = new JLabel("Time :");
        timeLabel.setFont(labelFont);
        topFields.add(timeLabel);
        topFields.add(hourCombo);
        
        // Date (Display only)
        JLabel dateLabel = new JLabel("Date :");
        dateLabel.setFont(labelFont);
        topFields.add(dateLabel);
        JLabel dateValue = new JLabel(event.getDate().toString());
        dateValue.setFont(fieldFont);
        topFields.add(dateValue);
        
        // --- Description Panel (BorderLayout) ---
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(getContentPane().getBackground());
        JLabel descLabel = new JLabel("Description :");
        descLabel.setFont(labelFont);
        descPanel.add(descLabel, BorderLayout.NORTH);
        
        descriptionArea = new JTextArea(event.getDescription(), 6, 30);
        descriptionArea.setFont(fieldFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        // --- Center Panel Combining Top and Description ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        centerPanel.add(topFields, BorderLayout.NORTH);
        centerPanel.add(descPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(getContentPane().getBackground());
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 100, 100)); 
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        deleteButton.setPreferredSize(new Dimension(90, 35));
        
        modifyButton = new JButton("Modify");
        modifyButton.setBackground(new Color(100, 180, 255)); 
        modifyButton.setForeground(Color.WHITE);
        modifyButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        modifyButton.setPreferredSize(new Dimension(90, 35));
        
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        closeButton.setPreferredSize(new Dimension(90, 35));

        buttonPanel.add(deleteButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Actions ---
        modifyButton.addActionListener(e -> modifyEvent());
        deleteButton.addActionListener(e -> deleteEvent());
        closeButton.addActionListener(e -> dispose());
    }

    private void modifyEvent() {
        String newTitle = titleField.getText().trim();
        String newResponsible = (String) responsibleCombo.getSelectedItem();
        String newTime = (String) hourCombo.getSelectedItem();
        String newDescription = descriptionArea.getText().trim();
        
        if (newTitle.isEmpty() || newResponsible.equals("Select Responsible") || newDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Description, and Responsible cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call controller to update the model with the new description
        boolean success = controller.modifyEventDetails(originalEvent, newTitle, newTime, newResponsible, newDescription);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Event modified successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Error updating the event.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEvent() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this event?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Call controller to update the model
            boolean success = controller.deleteEvent(originalEvent);
            if (success) {
                JOptionPane.showMessageDialog(this, "Event Deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error during deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
