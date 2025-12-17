package com.agenda.vue;

import com.agenda.model.Event;

import javax.swing.table.AbstractTableModel;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.List;

public class TableModel extends AbstractTableModel {

    private List<Event> events;
    private final String[] columnNames = {"Title", "Date", "Responsible"};

    public TableModel(List<Event> events) {
        this.events = events;
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col == 1) return LocalDate.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Event ev = events.get(row);
        switch (col) {
            case 0: return ev.getTitle();
            case 1: return ev.getDate();
            case 2: return ev.getResponsible();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0 || col == 1 || col == 2; // title, date, responsible
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Event ev = events.get(row);
        String val = String.valueOf(value).trim();
        switch (col) {
            case 0: ev.setTitle(val); break;
            case 1:
                try {
                    LocalDate newDate = LocalDate.parse(val);
                    ev.setDate(newDate);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid date format. Use yyyy-MM-dd",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2: ev.setResponsible(val); break;
        }
        fireTableCellUpdated(row, col);
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        fireTableDataChanged();
    }

    public Event getEventAt(int row) {
        return events.get(row);
    }
}