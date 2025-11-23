import javax.swing.*;
import java.awt.*;

public class CarTileView {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Car List - Tile View");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            // Panel with grid layout for tiles
            JPanel gridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Example car tiles (replace with your own images)
            gridPanel.add(createCarTile("Sedan", "D:\\University Of Sri Jayawaradhenapura\\2nd Semester\\OOP\\Project\\RentalProj\\test\\(1).jpg"));
            gridPanel.add(createCarTile("SUV", "(2).jpg"));
            gridPanel.add(createCarTile("Sports Car", "(3).jpg"));
            gridPanel.add(createCarTile("Truck", "(4).jpg"));

            frame.setContentPane(new JScrollPane(gridPanel));
            frame.setVisible(true);
        });
    }

    // Helper method to create a tile with image + label
    private static JPanel createCarTile(String carName, String imagePath) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Load image (make sure the file exists in your project folder)
        ImageIcon icon = new ImageIcon(imagePath);
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel nameLabel = new JLabel(carName, JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        tile.add(imageLabel, BorderLayout.CENTER);
        tile.add(nameLabel, BorderLayout.SOUTH);

        return tile;
    }

    public static Object showCarTiles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showCarTiles'");
    }
}
