package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class WeeklyGridPanel extends JPanel {

    private final AgendaController controller;
    private LocalDate weekStart;
    private final Runnable refreshCallback;
    private MainFrame mainFrame;

    private static final int START_HOUR = 7;
    private static final int END_HOUR = 18;
    
    // Zebra striping colors (same as EventListPanel)
    private static final Color EVEN_ROW_COLOR = Color.WHITE;
    private static final Color ODD_ROW_COLOR = new Color(255, 250, 205); // Faded Gold (Cornsilk)
    private static final Color HEADER_BG_COLOR = new Color(70, 130, 180);
    private static final Color WEEKEND_HEADER_BG_COLOR = new Color(30, 80, 150); // Darker blue for weekend
    private static final Color CELL_BORDER_COLOR = Color.LIGHT_GRAY;
    private static final Color WEEKEND_CELL_COLOR = new Color(180, 210, 240); // Light blue for weekend cells

    public WeeklyGridPanel(AgendaController controller, LocalDate weekStart, Runnable refreshCallback) {
        this.controller = controller;
        this.weekStart = weekStart;
        this.refreshCallback = refreshCallback;

        setLayout(new GridLayout(END_HOUR - START_HOUR + 2, 8, 1, 1)); // +2 for header row
        setBackground(CELL_BORDER_COLOR);

        // Try to detect MainFrame parent
        SwingUtilities.invokeLater(() -> {
            Component comp = SwingUtilities.getWindowAncestor(this);
            if (comp instanceof MainFrame) mainFrame = (MainFrame) comp;
        });

        repopulateGrid();
    }

    public void updateWeek(LocalDate newWeekStart) {
        this.weekStart = newWeekStart;
        repopulateGrid();
    }

    public void repopulateGrid() {
        removeAll();

        // --- Header row ---
        add(createCellLabel("Time", true, true, 0));
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            add(createCellLabel(date.getDayOfWeek() + " (" + date.toString() + ")", true, true, i + 1));
        }

        // --- Time rows with zebra striping ---
        for (int h = START_HOUR; h <= END_HOUR; h++) {
            String hourLabel = String.format("%02d:00-%02d:00", h, h + 1);
            boolean isEvenRow = (h - START_HOUR) % 2 == 0;
            Color rowColor = isEvenRow ? EVEN_ROW_COLOR : ODD_ROW_COLOR;
            
            // Time label cell
            add(createCellLabel(hourLabel, true, isEvenRow, 0));

            for (int d = 0; d < 7; d++) {
                LocalDate date = weekStart.plusDays(d);
                JPanel cell = createGridCell(date, h, rowColor, d + 1);
                add(cell);
            }
        }

        revalidate();
        repaint();
    }

    private JPanel createGridCell(LocalDate date, int hour, Color backgroundColor, int columnIndex) {
        JPanel cell = new JPanel(null);
        
        // Check if this is Friday (5) or Saturday (6) - weekend
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=Monday, 5=Friday, 6=Saturday
        
        // Determine cell color
        Color cellBgColor;
        if (dayOfWeek == 5 || dayOfWeek == 6) {
            // Friday or Saturday - weekend
            // Start with the zebra color, then blend with weekend color
            Color baseColor = backgroundColor;
            // Blend the colors: 70% weekend color, 30% zebra color
            cellBgColor = new Color(
                (int)(WEEKEND_CELL_COLOR.getRed() * 0.7 + baseColor.getRed() * 0.3),
                (int)(WEEKEND_CELL_COLOR.getGreen() * 0.7 + baseColor.getGreen() * 0.3),
                (int)(WEEKEND_CELL_COLOR.getBlue() * 0.7 + baseColor.getBlue() * 0.3)
            );
        } else {
            cellBgColor = backgroundColor; // Regular zebra color
        }
        
        cell.setBackground(cellBgColor);
        cell.setBorder(BorderFactory.createLineBorder(CELL_BORDER_COLOR));

        // Mouse listener
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Right-click → show context menu if an event exists in cell
                if (SwingUtilities.isRightMouseButton(e)) {
                    List<Event> eventsInCell = getEventsInCell(date, hour);
                    if (!eventsInCell.isEmpty() && mainFrame != null) {
                        // Show menu for the first event (simplest)
                        JPopupMenu menu = mainFrame.createEventContextMenu(eventsInCell.get(0));
                        menu.show(cell, e.getX(), e.getY());
                    }
                    return;
                }

                // Left-click → add event only if empty space
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (getEventsInCell(date, hour).isEmpty()) {
                        EventDialogUnified dialog = new EventDialogUnified(
                                (Frame) SwingUtilities.getWindowAncestor(WeeklyGridPanel.this),
                                controller,
                                EventDialogUnified.Mode.ADD,
                                null
                        );
                        dialog.setDateAndTime(date, String.format("%02d:00", hour));
                        dialog.setVisible(true);
                        refreshCallback.run();
                    }
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (getEventsInCell(date, hour).isEmpty()) {
                    cell.setBackground(cellBgColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cell.setBackground(cellBgColor);
            }
        });

        // Display events in this cell
        List<Event> eventsInCell = getEventsInCell(date, hour);
        for (Event event : eventsInCell) {
            JLabel eventLabel = new JLabel(event.getTitle() + " (" + event.getTime() + ")", SwingConstants.CENTER);
            eventLabel.setToolTipText("<html>" + event.getDescription() + "<br>Responsible: " + event.getResponsible() + "</html>");
            eventLabel.setBackground(new Color(200, 220, 240));
            eventLabel.setForeground(Color.DARK_GRAY);
            eventLabel.setOpaque(true);
            eventLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            eventLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eventLabel.setBounds(5, 5, 120, 25);
            cell.add(eventLabel);

            // Left-click → edit
            eventLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    e.consume(); // prevent cell click
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        EventDialogUnified dialog = new EventDialogUnified(
                                (Frame) SwingUtilities.getWindowAncestor(WeeklyGridPanel.this),
                                controller,
                                EventDialogUnified.Mode.EDIT,
                                event
                        );
                        dialog.setVisible(true);
                        refreshCallback.run();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) && mainFrame != null) {
                        JPopupMenu menu = mainFrame.createEventContextMenu(event);
                        menu.show(eventLabel, e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) && mainFrame != null) {
                        JPopupMenu menu = mainFrame.createEventContextMenu(event);
                        menu.show(eventLabel, e.getX(), e.getY());
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    eventLabel.setBackground(new Color(180, 200, 230)); // Slightly darker on hover
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    eventLabel.setBackground(new Color(200, 220, 240)); // Original color
                }
            });

            // Drag & Drop
            MouseInputAdapter dragAdapter = createDragAdapter(event, eventLabel);
            eventLabel.addMouseListener(dragAdapter);
            eventLabel.addMouseMotionListener(dragAdapter);
        }

        return cell;
    }

    private List<Event> getEventsInCell(LocalDate date, int hour) {
        return controller.getEvents().stream()
                .filter(ev -> ev.getDate().equals(date))
                .filter(ev -> {
                    try {
                        return Integer.parseInt(ev.getTime().split(":")[0]) == hour;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    private MouseInputAdapter createDragAdapter(Event event, JLabel eventLabel) {
        return new MouseInputAdapter() {
            Point pressPoint;
            JLabel floatingLabel;
            boolean dragging = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) return; // skip right-click
                pressPoint = e.getPoint();
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WeeklyGridPanel.this);
                if (frame == null) return;
                JComponent glassPane = (JComponent) frame.getGlassPane();
                glassPane.setVisible(true);
                glassPane.setLayout(null);

                floatingLabel = new JLabel(eventLabel.getText(), SwingConstants.CENTER);
                floatingLabel.setOpaque(true);
                floatingLabel.setBackground(eventLabel.getBackground());
                floatingLabel.setForeground(eventLabel.getForeground());
                floatingLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                floatingLabel.setSize(eventLabel.getSize());
                floatingLabel.setFont(eventLabel.getFont());
                floatingLabel.setHorizontalAlignment(SwingConstants.CENTER);

                Point labelPoint = SwingUtilities.convertPoint(eventLabel, e.getPoint(), glassPane);
                floatingLabel.setLocation(labelPoint.x - pressPoint.x, labelPoint.y - pressPoint.y);

                eventLabel.setVisible(false);
                glassPane.add(floatingLabel);
                glassPane.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (floatingLabel == null) return;
                dragging = true;

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WeeklyGridPanel.this);
                if (frame == null) return;
                JComponent glassPane = (JComponent) frame.getGlassPane();

                Point p = SwingUtilities.convertPoint(eventLabel, e.getPoint(), glassPane);
                floatingLabel.setLocation(p.x - pressPoint.x, p.y - pressPoint.y);
                glassPane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WeeklyGridPanel.this);
                if (frame == null) return;
                JComponent glassPane = (JComponent) frame.getGlassPane();

                if (floatingLabel != null) {
                    glassPane.remove(floatingLabel);
                    glassPane.repaint();
                }
                eventLabel.setVisible(true);

                if (!dragging) return;
                dragging = false;

                Point dropPoint = SwingUtilities.convertPoint(eventLabel, e.getPoint(), WeeklyGridPanel.this);
                Component[] comps = WeeklyGridPanel.this.getComponents();
                int targetIndex = -1;
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i].getBounds().contains(dropPoint)) {
                        targetIndex = i;
                        break;
                    }
                }

                if (targetIndex == -1 || targetIndex < 8 || targetIndex % 8 == 0) {
                    refreshCallback.run();
                    return;
                }

                int row = targetIndex / 8;
                int col = targetIndex % 8;
                int targetHour = START_HOUR + (row - 1);
                LocalDate targetDate = weekStart.plusDays(col - 1);

                int minutes = Integer.parseInt(event.getTime().split(":")[1]);
                String newTime = String.format("%02d:%02d", targetHour, minutes);

                boolean success = controller.updateEventDateTime(event, targetDate, newTime);
                if (!success) {
                    Event newEvent = new Event(event.getTitle(), targetDate, newTime,
                            event.getDescription(), event.getResponsible());
                    if (controller.deleteEvent(event)) {
                        controller.addEvent(newEvent);
                    }
                }

                refreshCallback.run();
            }
        };
    }

    private JLabel createCellLabel(String text, boolean isHeader, boolean isEvenRow, int columnIndex) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(CELL_BORDER_COLOR));
        
        if (isHeader) {
            // Column 0 is Time column, columns 5 and 6 are Friday and Saturday (weekend)
            // Note: columnIndex 0 = Time, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 
            //       4 = Thursday, 5 = Friday, 6 = Saturday, 7 = Sunday
            
            if (columnIndex == 5 || columnIndex == 6) { // Friday or Saturday
                label.setBackground(WEEKEND_HEADER_BG_COLOR); // Darker blue for weekend headers
            } else {
                label.setBackground(HEADER_BG_COLOR); // Regular blue for weekday headers
            }
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
        } else {
            // Time column cells - zebra striping
            Color bgColor = isEvenRow ? EVEN_ROW_COLOR : ODD_ROW_COLOR;
            label.setBackground(bgColor);
            label.setOpaque(true);
            label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        }
        return label;
    }
}
