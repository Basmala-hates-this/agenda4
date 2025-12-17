

//package com.agenda.vue;
//
//import com.agenda.controler.AgendaController;
//import javax.swing.SwingUtilities;
//
//public class Main {
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            AgendaController controller = new AgendaController();
//            MainFrame frame = new MainFrame(controller);
//            frame.setVisible(true);
//        });
//    }
//}

package com.agenda.vue;

import com.agenda.controler.AgendaController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create single AgendaController instance
            AgendaController controller = new AgendaController();
            
            // Show welcome page first
            WelcomePage welcomePage = new WelcomePage();
            welcomePage.setVisible(true);
            
            // Or directly show MainFrame if you want
            // MainFrame frame = new MainFrame(controller);
            // frame.setVisible(true);
        });
    }
}