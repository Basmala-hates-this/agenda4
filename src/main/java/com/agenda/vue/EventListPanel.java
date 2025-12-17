package com.agenda.vue;

import com.agenda.controler.AgendaController;
import com.agenda.model.Event;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class EventListPanel extends JPanel {

    private final AgendaController controller;
    private final Runnable refreshCallback;

    // Define the colors for zebra striping
    private static final Color EVEN_ROW_COLOR = Color.WHITE;
    private static final Color ODD_ROW_COLOR = new Color(255, 250, 205); // Faded Gold (Cornsilk)
    private static final Color HEADER_BG_COLOR = new Color(70, 130, 180);

    private JTable table;
    private TableModel tableModel;
    private JTextField searchField;
    private JButton deleteButton;
    private JComboBox<String> filterCombo;

    private javax.swing.Timer reminderTimer; 

    public EventListPanel(AgendaController controller, Runnable refreshCallback) {
        this.controller = controller;
        this.refreshCallback = refreshCallback;

        setLayout(new BorderLayout());

        // ============================
        // TOP BAR (filter/search/delete)
        // ============================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(220, 235, 255)); 

        filterCombo = new JComboBox<>(new String[]{"All", "Title", "Date", "Responsible"});
        topPanel.add(filterCombo);

        searchField = new JTextField(30);
        topPanel.add(searchField);

        deleteButton = new JButton("Delete");
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);

        // ============================
        // TABLE (Zebra Striping Logic Included)
        // ============================
        tableModel = new TableModel(controller.getEvents());
        table = new JTable(tableModel) {
            
            // Custom renderer logic for Zebra Striping
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (isRowSelected(row)) {
                    return c;
                }
                
                if (row % 2 == 0) { 
                    c.setBackground(EVEN_ROW_COLOR);
                } else { 
                    c.setBackground(ODD_ROW_COLOR);
                }
                
                c.setForeground(Color.BLACK);
                return c;
            }
        };
        table.setFillsViewportHeight(true);
        table.setGridColor(Color.LIGHT_GRAY);
        
        table.getTableHeader().setBackground(HEADER_BG_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));

        table.setSelectionBackground(new Color(200, 220, 240));
        table.setSelectionForeground(Color.BLACK);

        table.setBackground(EVEN_ROW_COLOR); 

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ============================
        // SORT + FILTER
        // ============================
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateFilter(); }
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            public void insertUpdate(DocumentEvent e) { updateFilter(); }

            private void updateFilter() {
                String text = searchField.getText();
                int col = filterCombo.getSelectedIndex() - 1;

                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                    updateStatusOnly("Search cleared");
                } else if (col < 0) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    updateStatusOnly("Searching all fields for: " + text);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, col));
                    String[] columns = {"Title", "Date", "Responsible"};
                    updateStatusOnly("Searching " + columns[col] + " for: " + text);
                }
            }
        });

        // ============================
        // DELETE BUTTON
        // ============================
        deleteButton.addActionListener(e -> deleteSelectedEvent());

        // ============================
        // CONTEXT MENU: Modify/Delete/Share
        // ============================
        JPopupMenu menu = new JPopupMenu();
        JMenuItem modifyItem = new JMenuItem("Modify");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem shareItem = new JMenuItem("Share");
        menu.add(modifyItem);
        menu.add(deleteItem);
        menu.add(shareItem);
        table.setComponentPopupMenu(menu);

        modifyItem.addActionListener(e -> modifyEvent());
        deleteItem.addActionListener(e -> deleteSelectedEvent());
        shareItem.addActionListener(e -> shareEvent());

        // ============================
        // KEYBOARD DELETE SHORTCUT
        // ============================
        table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("DELETE"), "deleteEvent");
        table.getActionMap().put("deleteEvent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEvent();
            }
        });

        // ============================
        // REMINDER TIMER 
        // ============================
        // Uses 10 seconds interval as this timer only updates the status bar next event text
        reminderTimer = new javax.swing.Timer(10_000, e -> checkNextEvent());
        reminderTimer.setInitialDelay(0);
        reminderTimer.start();
        checkNextEvent();
    }

    // ============================
    // GET SELECTED EVENT
    // ============================
    public Event getSelectedEvent() {
        int r = table.getSelectedRow();
        if (r == -1) return null;
        int model = table.convertRowIndexToModel(r);
        return tableModel.getEventAt(model);
    }

    // ============================
    // DELETE EVENT (FIXED STATUS UPDATE)
    // ============================
    private void deleteSelectedEvent() {
        Event ev = getSelectedEvent();
        if (ev == null) {
            showNotification("No event selected for deletion");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteEvent(ev);
            
            // 1. Refresh the list model first
            refreshCallback.run(); 
            
            // 2. FIX: Send success notification immediately to update the status bar 
            // before any further UI events override it.
            showNotification("Event deleted successfully");
        } else {
            showNotification("Deletion canceled");
        }
    }

    // ============================
    // MODIFY EVENT
    // ============================
    private void modifyEvent() {
        Event ev = getSelectedEvent();
        if (ev == null) {
            showNotification("No event selected for modification");
            return;
        }
        // SwingUtilities.getWindowAncestor(this) returns the owning JFrame (MainFrame)
        showNotification("Modifying event: " + ev.getTitle());
        new EventDialogUnified((Frame) SwingUtilities.getWindowAncestor(this), controller, EventDialogUnified.Mode.EDIT, ev)
                .setVisible(true);
        refreshCallback.run(); 
    }

    // ============================
    // SHARE EVENT
    // ============================
    private void shareEvent() {
        Event ev = getSelectedEvent();
        if (ev == null) {
            showNotification("No event selected to share");
            return;
        }

        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            List<String> selectedUsers = mainFrame.showUnifiedShareDialog(this, "Share Event: " + ev.getTitle());
            
            if (selectedUsers != null) {
                mainFrame.notifyShare(selectedUsers.size());
            } else {
                mainFrame.notifyActionCanceled("Sharing");
            }
        }
    }

    // ============================
    // REFRESH TABLE
    // ============================
    public void refreshList() {
        tableModel.setEvents(controller.getEvents());
        showNotification("List view refreshed"); 
    }

    // ============================
    // NEXT EVENT REMINDER
    // ============================
    private void checkNextEvent() {
        Event next = controller.getNextEvent();
        // Since the requirement is about the MainFrame status bar, we use the callback
        if (next != null && refreshCallback instanceof MainFrame mf) {
            mf.setStatus("Next event: " + next.getTitle() + " at " + next.getTime());
        }
    }

    // ============================
    // HELPER NOTIFICATION (TOAST INTEGRATION)
    // ============================
    private void showNotification(String msg) {
        // FIX: Ensure the callback is correctly cast and notifyToast is called
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            mainFrame.setStatus(msg);
            mainFrame.notifyToast(msg);
        } else {
            System.out.println("Notification: " + msg);
        }
    }

    // ============================
    // UPDATE STATUS WITHOUT TOAST
    // ============================
    private void updateStatusOnly(String msg) {
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            mainFrame.setStatus(msg); // Only update status bar, no toast
        }
    }
}
