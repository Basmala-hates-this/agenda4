package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EventDialogUnified extends JDialog {

    public enum Mode { ADD, EDIT, VIEW }

    private Mode mode;
    private AgendaController controller;
    private Event event;
    
    // Status update reference
    private MainFrame ownerFrame; 

    // Components
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> responsibleCombo;
    private JPanel participantsPanel;
    private ArrayList<JCheckBox> participantCheckboxes;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;

    private JButton actionButton;
    private JButton deleteButton;
    private JButton cancelButton;

    // Sample users (Restored)
    private final String[] allUsers = {
            "Hello", "Hii", "Hey", "You", "Better", "Say", "You", "Like it",
            "And", "Say", "That", "It", "Completly", "Works", "OR ELSE", "Never Mind",
            "Just", "Be A", "Good ", "Person","And","Say","The App","Works","Thank U","- :) -"
        };

    // Colors
    private final Color BG_BLUE = new Color(200, 220, 240);
    private final Color GOLD = new Color(218, 165, 32);
    private final Color BUTTON_BLUE = new Color(100, 180, 255);

    public EventDialogUnified(Frame owner, AgendaController controller, Mode mode, Event event) {
        super(owner, true);
        this.controller = controller;
        this.mode = mode;
        this.event = event;
        
        // FIX: Store the MainFrame reference for status updates
        if (owner instanceof MainFrame) {
            this.ownerFrame = (MainFrame) owner;
        }

        setTitle(mode == Mode.ADD ? "Add Event" : mode == Mode.EDIT ? "Edit Event" : "View Event");
        setSize(500, 650); // Increased height for better layout
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(GOLD);
        JLabel headerLabel = new JLabel(getTitle());
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(headerLabel);
        add(header, BorderLayout.NORTH);

        initComponents();

        // Load data if editing/viewing
        if (event != null) loadEventData();
        if (mode == Mode.VIEW) disableEditing();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); // More padding
        mainPanel.setBackground(BG_BLUE);

        // Create a consistent font for labels and fields
        Font labelFont = new Font("SansSerif", Font.BOLD, 14); // Bigger labels
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        Dimension fieldSize = new Dimension(420, 35); // All fields same width
        
        // Title - LEFT ALIGNED and BIGGER
        JLabel titleLabel = new JLabel("Title *");
        titleLabel.setFont(labelFont);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(titleLabel);
        
        titleField = new JTextField();
        titleField.setFont(fieldFont);
        titleField.setMaximumSize(fieldSize);
        titleField.setPreferredSize(fieldSize);
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleField);

        // Description - LEFT ALIGNED
        mainPanel.add(Box.createVerticalStrut(15));
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(labelFont);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(descLabel);
        
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setFont(fieldFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setMaximumSize(new Dimension(420, 100));
        descScroll.setPreferredSize(new Dimension(420, 100));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descScroll);

        // Responsible - LEFT ALIGNED
        mainPanel.add(Box.createVerticalStrut(15));
        JLabel respLabel = new JLabel("Responsible");
        respLabel.setFont(labelFont);
        respLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        respLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(respLabel);
        
        responsibleCombo = new JComboBox<>(allUsers);
        responsibleCombo.setFont(fieldFont);
        responsibleCombo.setMaximumSize(fieldSize);
        responsibleCombo.setPreferredSize(fieldSize);
        responsibleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(responsibleCombo);

        // Participants - LEFT ALIGNED
        mainPanel.add(Box.createVerticalStrut(15));
        JLabel partLabel = new JLabel("Participants");
        partLabel.setFont(labelFont);
        partLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        partLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(partLabel);
        
        participantsPanel = new JPanel();
        participantsPanel.setLayout(new BoxLayout(participantsPanel, BoxLayout.Y_AXIS));
        participantsPanel.setBackground(BG_BLUE);
        participantCheckboxes = new ArrayList<>();
        
        // Use larger font for checkboxes
        Font checkboxFont = new Font("SansSerif", Font.PLAIN, 13);
        for (String user : allUsers) {
            JCheckBox cb = new JCheckBox(user);
            cb.setFont(checkboxFont);
            cb.setBackground(BG_BLUE);
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            participantCheckboxes.add(cb);
            participantsPanel.add(cb);
        }
        JScrollPane partScroll = new JScrollPane(participantsPanel);
        partScroll.setMaximumSize(new Dimension(420, 120));
        partScroll.setPreferredSize(new Dimension(420, 120));
        partScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(partScroll);

        // Date - LEFT ALIGNED
        mainPanel.add(Box.createVerticalStrut(15));
        JLabel dateLabel = new JLabel("Date *");
        dateLabel.setFont(labelFont);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(dateLabel);
        
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setFont(fieldFont);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        
        // Make spinner editor larger and same width
        JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) dateSpinner.getEditor();
        dateEditor.getTextField().setFont(fieldFont);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) dateSpinner.getEditor();
        editor.getTextField().setMaximumSize(fieldSize);
        editor.getTextField().setPreferredSize(fieldSize);
        
        dateSpinner.setMaximumSize(fieldSize);
        dateSpinner.setPreferredSize(fieldSize);
        dateSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(dateSpinner);

        // Time - LEFT ALIGNED
        mainPanel.add(Box.createVerticalStrut(15));
        JLabel timeLabel = new JLabel("Time *");
        timeLabel.setFont(labelFont);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        mainPanel.add(timeLabel);
        
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setFont(fieldFont);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        
        // Make spinner editor larger and same width
        JSpinner.DateEditor timeEditor = (JSpinner.DateEditor) timeSpinner.getEditor();
        timeEditor.getTextField().setFont(fieldFont);
        JSpinner.DefaultEditor timeSpinnerEditor = (JSpinner.DefaultEditor) timeSpinner.getEditor();
        timeSpinnerEditor.getTextField().setMaximumSize(fieldSize);
        timeSpinnerEditor.getTextField().setPreferredSize(fieldSize);
        
        timeSpinner.setMaximumSize(fieldSize);
        timeSpinner.setPreferredSize(fieldSize);
        timeSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (mode == Mode.ADD) setTimeToNoon();
        mainPanel.add(timeSpinner);

        // Buttons - FIXED: Proper right alignment
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Create a container for buttons with proper right alignment
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(BG_BLUE);
        buttonContainer.setMaximumSize(new Dimension(420, 50));
        buttonContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Left filler to push buttons to the right
        JPanel leftFiller = new JPanel();
        leftFiller.setBackground(BG_BLUE);
        buttonContainer.add(leftFiller, BorderLayout.CENTER);
        
        // Button panel on the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BG_BLUE);
        
        actionButton = new JButton(mode == Mode.ADD ? "Add" : "Save");
        styleButton(actionButton, BUTTON_BLUE);
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        actionButton.setPreferredSize(new Dimension(90, 35));
        
        deleteButton = new JButton("Delete");
        styleButton(deleteButton, Color.RED);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        deleteButton.setPreferredSize(new Dimension(90, 35));
        
        cancelButton = new JButton("Cancel");
        styleButton(cancelButton, GOLD);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelButton.setPreferredSize(new Dimension(90, 35));

        actionButton.addActionListener(e -> onSave());
        deleteButton.addActionListener(e -> onDelete());
        
        // FIX: Update status bar on cancel
        cancelButton.addActionListener(e -> {
            if (ownerFrame != null) {
                if (mode == Mode.ADD) {
                    ownerFrame.notifyActionCanceled("Event creation");
                } else if (mode == Mode.EDIT) {
                    ownerFrame.notifyActionCanceled("Event modification"); 
                }
            }
            dispose();
        });

        buttonPanel.add(actionButton);
        if (mode == Mode.EDIT) buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        
        buttonContainer.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(buttonContainer);

        // Add everything to the dialog
        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13)); // Slightly larger
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { 
                btn.setBackground(btn.getBackground().darker()); 
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(btn.getBackground().darker(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { 
                btn.setBackground(color);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.darker(), 1),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
            }
        });
    }

    private void loadEventData() {
        titleField.setText(event.getTitle());
        descriptionArea.setText(event.getDescription());
        responsibleCombo.setSelectedItem(event.getResponsible());
        dateSpinner.setValue(java.sql.Date.valueOf(event.getDate()));
        
        // FIX: Ensure time parsing is safe
        try {
             timeSpinner.setValue(java.sql.Time.valueOf(LocalTime.parse(event.getTime())));
        } catch (Exception e) {
             setTimeToNoon(); // Default to noon if time is corrupted
        }


        for (JCheckBox cb : participantCheckboxes) {
            // FIX: Check if getParticipants() is null before calling contains()
            if (event.getParticipants() != null && event.getParticipants().contains(cb.getText())) {
                cb.setSelected(true);
            }
        }
    }

    private void disableEditing() {
        titleField.setEnabled(false);
        descriptionArea.setEnabled(false);
        responsibleCombo.setEnabled(false);
        for (JCheckBox cb : participantCheckboxes) cb.setEnabled(false);
        dateSpinner.setEnabled(false);
        timeSpinner.setEnabled(false);
        actionButton.setVisible(false);
        deleteButton.setVisible(false);
        cancelButton.setText("Close");
    }

    private void setTimeToNoon() {
        try {
            timeSpinner.setValue(java.sql.Time.valueOf("12:00"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onSave() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required! dumbass", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String responsible = (String) responsibleCombo.getSelectedItem();
        ArrayList<String> participants = new ArrayList<>();
        for (JCheckBox cb : participantCheckboxes) if (cb.isSelected()) participants.add(cb.getText());

        LocalDate date = LocalDate.parse(new java.text.SimpleDateFormat("yyyy-MM-dd").format(dateSpinner.getValue()));
        String time = new java.text.SimpleDateFormat("HH:mm").format(timeSpinner.getValue());

        if (mode == Mode.ADD) {
            Event newEvent = new Event(title, date, time, description, responsible);
            newEvent.setParticipants(participants);
            controller.addEvent(newEvent);
            
            // FIX: Update status bar instead of showMessageDialog
            if (ownerFrame != null) {
                ownerFrame.notifyEventCreated();
            } else {
                JOptionPane.showMessageDialog(this, "Event added successfully! yeeey,u r -better not say that- yeeey");
            }
        } else if (mode == Mode.EDIT) {
            event.setTitle(title);
            event.setDescription(description);
            event.setResponsible(responsible);
            event.setDate(date);
            event.setTime(time);
            event.setParticipants(participants);
            
            controller.modifyEventDetails(event, title, time, responsible, description); 
            
            // FIX: Update status bar instead of showMessageDialog
            if (ownerFrame != null) {
                ownerFrame.notifyEventModified();
            } else {
                JOptionPane.showMessageDialog(this, "Event updated successfully! why tho?");
            }
        }

        dispose();
    }

    private void onDelete() {
         if (event == null) return;
           int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this event? it looks fun",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
        
        // 1. Delete the event from the controller
          controller.deleteEvent(event);
        
        // 2. FIX: Update the status bar BEFORE disposing the dialog
           if (ownerFrame != null) {
            ownerFrame.notifyEventDeleted();
        }
        
        // 3. Dispose the dialog
        dispose();
    } else {
        if (ownerFrame != null) {
            ownerFrame.notifyActionCanceled("Deletion");
        }
    }
}

    public void setDate(LocalDate date) {
        dateSpinner.setValue(java.sql.Date.valueOf(date));
    }

    public void setDateAndTime(LocalDate date, String time) {
        dateSpinner.setValue(java.sql.Date.valueOf(date));
        if (time == null || time.isEmpty()) {
            setTimeToNoon();
        } else {
            // FIX: Ensure time parsing is safe
            try {
                 timeSpinner.setValue(java.sql.Time.valueOf(LocalTime.parse(time)));
            } catch (Exception e) {
                setTimeToNoon();
            }
        }
    }
}
