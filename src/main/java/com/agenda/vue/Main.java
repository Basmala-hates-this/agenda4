

package com.agenda.vue;

import com.agenda.controler.AgendaController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AgendaController controller = new AgendaController();
            MainFrame frame = new MainFrame(controller);
            frame.setVisible(true);
        });
    }
}
