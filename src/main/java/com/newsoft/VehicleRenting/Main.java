package main.java.com.newsoft.VehicleRenting;

import javax.swing.SwingUtilities;
import main.java.com.newsoft.VehicleRenting.ui.VehicleRentalUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // launch the welcome screen that then opens VehicleRentalUI in your project
            // if you want to directly open the UI:
            VehicleRentalUI ui = new VehicleRentalUI();
            ui.setVisible(true);
        });
    }
}
