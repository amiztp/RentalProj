import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Vehicles");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            // 2x2 grid for four buttons
            JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
            panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            JButton carsBtn = new JButton("Cars");
            JButton vansBtn = new JButton("Vans");
            JButton bikesBtn = new JButton("Bikes");
            JButton lorriesBtn = new JButton("Lorries");

            // Example actions (replace with your handlers)
            carsBtn.addActionListener(e -> System.out.println("Cars clicked"));
            carsBtn.addActionListener(e -> CarTileView.showCarTiles()); 
            vansBtn.addActionListener(e -> System.out.println("Vans clicked"));
            bikesBtn.addActionListener(e -> System.out.println("Bikes clicked"));
            lorriesBtn.addActionListener(e -> System.out.println("Lorries clicked"));

            panel.add(carsBtn);
            panel.add(vansBtn);
            panel.add(bikesBtn);
            panel.add(lorriesBtn);

            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }
}
