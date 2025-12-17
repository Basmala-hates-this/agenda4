package com.agenda.vue;

import com.agenda.model.Event;
import java.awt.datatransfer.*;

public class EventTransferable implements Transferable {
    private Event event;
    public static final DataFlavor EVENT_FLAVOR = new DataFlavor(Event.class, "Agenda Event");

    public EventTransferable(Event event) { this.event = event; }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{EVENT_FLAVOR, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(EVENT_FLAVOR) || flavor.equals(DataFlavor.stringFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(EVENT_FLAVOR)) return event;
        else if (flavor.equals(DataFlavor.stringFlavor)) return event.toString();
        throw new UnsupportedFlavorException(flavor);
    }
}