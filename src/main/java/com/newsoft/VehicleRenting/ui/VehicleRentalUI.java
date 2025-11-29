package main.java.com.newsoft.VehicleRenting.ui;

import main.java.com.newsoft.VehicleRenting.model.*;
import main.java.com.newsoft.VehicleRenting.service.VehicleService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VehicleRentalUI extends JFrame {
    
    private VehicleService vehicleService;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea;
    private JPanel photoPanel;
    private JPanel detailsPanel;
    private JPanel photoContainer;
    private JPanel rightPanel;
    
    public VehicleRentalUI() {
        vehicleService = new VehicleService();
        initializeUI();
        showDefaultLogo();
        loadVehicles();
    }
    
    private void initializeUI() {
        setTitle("Drive Smart - Vehicle Rental Management");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(24, 16)); // 24px gutter, 16px vertical spacing
        mainPanel.setBorder(new EmptyBorder(16, 16, 16, 16)); // 16px padding (2 units)
        mainPanel.setBackground(new Color(244, 247, 250)); // Page Background #F4F7FA
        
        // Top panel - Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(11, 111, 175)); // Primary Blue #0B6FAF
        titlePanel.setPreferredSize(new Dimension(0, 56)); // 56px height
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16)); // 8px vertical, 16px horizontal
        
        // Left: Logo + App Name
        JPanel leftTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftTitlePanel.setOpaque(false);
        
        // Small logo
        File logoFile = new File("data/photos/logo.png");
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
            Image logoImg = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // 40px height
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            leftTitlePanel.add(logoLabel);
        }
        
        JLabel appNameLabel = new JLabel("Drive Smart");
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); // H1 style
        appNameLabel.setForeground(Color.WHITE);
        leftTitlePanel.add(appNameLabel);
        
        // Center: Main Title
        JLabel titleLabel = new JLabel("Vehicle Rental Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // H1: 24pt Bold
        titleLabel.setForeground(Color.WHITE);
        
        // Right: Search panel and Home button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        
        // Home button
        JButton homeButton = new JButton("🏠 Home");
        homeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        homeButton.setForeground(Color.WHITE);
        homeButton.setBackground(new Color(9, 74, 122));
        homeButton.setPreferredSize(new Dimension(100, 32));
        homeButton.setFocusPainted(false);
        homeButton.setBorderPainted(false);
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.setToolTipText("Return to Welcome Screen");
        homeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                homeButton.setBackground(new Color(6, 53, 87));
            }
            public void mouseExited(MouseEvent e) {
                homeButton.setBackground(new Color(9, 74, 122));
            }
        });
        homeButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                WelcomeScreen welcomeScreen = new WelcomeScreen();
                welcomeScreen.setVisible(true);
            });
        });
        searchPanel.add(homeButton);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel);
        
        JTextField headerSearchField = new JTextField(20);
        headerSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headerSearchField.setPreferredSize(new Dimension(200, 32));
        headerSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        headerSearchField.setToolTipText("Search by registration, model, or type");
        
        headerSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = headerSearchField.getText().trim();
                performHeaderSearch(searchText);
            }
        });
        
        searchPanel.add(headerSearchField);
        
        titlePanel.add(leftTitlePanel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(searchPanel, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Center panel - Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(8); // 8px divider (1 unit)
        
        // Left panel - Vehicle table
        JPanel leftPanel = createTablePanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right panel - Details and photos
        JPanel rightPanel = createDetailsPanel();
        splitPane.setRightComponent(rightPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel - Buttons
        JPanel bottomPanel = createButtonPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenuItem searchItem = new JMenuItem("Search Vehicle");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        refreshItem.addActionListener(e -> loadVehicles());
        searchItem.addActionListener(e -> searchVehicle());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(refreshItem);
        fileMenu.add(searchItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(244, 247, 250)); // Sidebar Background #F4F7FA (page background)
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); // 16px padding only, no border
        
        // Add title label instead of titled border
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(244, 247, 250)); // Match sidebar background
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JLabel titleLabel = new JLabel("Vehicle List");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // H2 style
        titleLabel.setForeground(new Color(34, 40, 49)); // Primary Text
        headerPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"Type", "Model", "Availability"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vehicleTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (isRowSelected(row)) {
                    c.setBackground(new Color(217, 237, 255)); // Selected row #D9EDFF
                    c.setForeground(new Color(34, 40, 49)); // Primary Text #222831
                    
                    // Add left accent bar for selected row (4px) - visual indicator
                    if (column == 0) {
                        ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(11, 111, 175)), // 4px left accent
                            BorderFactory.createEmptyBorder(0, 4, 1, 0) // Additional padding + subtle bottom divider
                        ));
                    } else {
                        ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 222, 230, 100)), // Subtle horizontal divider
                            BorderFactory.createEmptyBorder(0, 8, 0, 8)
                        ));
                    }
                } else {
                    // Alternate row colors
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE); // #FFFFFF
                    } else {
                        c.setBackground(new Color(238, 243, 247)); // #EEF3F7
                    }
                    c.setForeground(new Color(34, 40, 49)); // Primary Text #222831
                    
                    // Add subtle horizontal divider to all cells
                    if (column == 0 && c instanceof JComponent) {
                        ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 222, 230, 100)), // Subtle horizontal divider
                            BorderFactory.createEmptyBorder(0, 8, 0, 0)
                        ));
                    } else if (c instanceof JComponent) {
                        ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 222, 230, 100)), // Subtle horizontal divider
                            BorderFactory.createEmptyBorder(0, 8, 0, 8)
                        ));
                    }
                }
                return c;
            }
        };
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setRowHeight(32); // 32px row height
        vehicleTable.setShowGrid(false); // No gridlines
        vehicleTable.setIntercellSpacing(new Dimension(0, 0)); // No spacing between cells
        vehicleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Body: 14pt Regular - scalable text
        
        // Style table header
        vehicleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold header - scalable
        vehicleTable.getTableHeader().setBackground(new Color(11, 111, 175)); // Primary Blue #0B6FAF
        vehicleTable.getTableHeader().setForeground(Color.WHITE); // White text on #0B6FAF - passes 4.5:1 contrast
        vehicleTable.getTableHeader().setPreferredSize(new Dimension(0, 40)); // Header height
        vehicleTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        vehicleTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        vehicleTable.getTableHeader().setResizingAllowed(false); // Disable column resizing
        
        // Disable header hover effect
        vehicleTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setBackground(new Color(11, 111, 175)); // Always keep DriveSmart Blue #0B6FAF
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                label.setOpaque(true);
                return label;
            }
        });
        
        // Custom selection border (left accent bar)
        vehicleTable.setSelectionBackground(new Color(217, 237, 255)); // #D9EDFF
        vehicleTable.setSelectionForeground(new Color(34, 40, 49)); // Primary Text #222831 - sufficient contrast
        
        // Keyboard focus outline for accessibility
        vehicleTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                vehicleTable.setBorder(BorderFactory.createLineBorder(new Color(0, 176, 216), 2)); // 2px solid #00B0D8
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                vehicleTable.setBorder(BorderFactory.createEmptyBorder());
            }
        });
        
        // Add selection listener
        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showVehicleDetails();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailsPanel() {
        rightPanel = new JPanel(new BorderLayout(0, 16)); // 16px vertical spacing (2 units)
        
        // Details text area
        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE); // Panel Background #FFFFFF
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(215, 222, 230), 1, true), // 1px solid border #D7DEE6, 6px radius
            BorderFactory.createEmptyBorder(16, 16, 16, 16) // 16px internal padding
        ));
        
        // Add title label
        JLabel detailsTitle = new JLabel("Vehicle Details");
        detailsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16)); // H2 style
        detailsTitle.setForeground(new Color(34, 40, 49)); // Primary Text
        detailsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        detailsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Body: 14pt Regular - scalable text
        detailsArea.setMargin(new Insets(0, 0, 0, 0)); // No extra margin, handled by panel padding
        detailsArea.setBackground(Color.WHITE); // Panel Background #FFFFFF
        detailsArea.setForeground(new Color(34, 40, 49)); // Primary Text #222831 on #FFFFFF - passes 4.5:1 contrast
        detailsArea.setBorder(BorderFactory.createEmptyBorder()); // No border
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        detailsScroll.setPreferredSize(new Dimension(400, 200));
        
        JPanel detailsContent = new JPanel(new BorderLayout());
        detailsContent.setBackground(Color.WHITE);
        detailsContent.add(detailsTitle, BorderLayout.NORTH);
        detailsContent.add(detailsScroll, BorderLayout.CENTER);
        detailsPanel.add(detailsContent, BorderLayout.CENTER);
        
        // Photos panel
        photoContainer = new JPanel(new BorderLayout());
        photoContainer.setBackground(Color.WHITE); // Panel Background #FFFFFF
        photoContainer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(215, 222, 230), 1, true), // 1px solid border #D7DEE6, 6px radius
            BorderFactory.createEmptyBorder(16, 16, 16, 16) // 16px internal padding
        ));
        
        // Add title label
        JLabel photosTitle = new JLabel("Vehicle Photos");
        photosTitle.setFont(new Font("Segoe UI", Font.BOLD, 16)); // H2 style
        photosTitle.setForeground(new Color(34, 40, 49)); // Primary Text
        photosTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        photoPanel = new JPanel(new GridLayout(0, 2, 16, 16)); // 2 columns, auto rows, 16px spacing
        photoPanel.setBackground(Color.WHITE); // Panel Background #FFFFFF
        JScrollPane photoScroll = new JScrollPane(photoPanel);
        photoScroll.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        photoScroll.setPreferredSize(new Dimension(400, 300));
        
        JPanel photosContent = new JPanel(new BorderLayout());
        photosContent.setBackground(Color.WHITE);
        photosContent.add(photosTitle, BorderLayout.NORTH);
        photosContent.add(photoScroll, BorderLayout.CENTER);
        photoContainer.add(photosContent, BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8)); // 16px horizontal, 8px vertical
        panel.setBackground(new Color(238, 243, 247)); // Light Muted #EEF3F7
        
        JButton refreshBtn = createPrimaryButton("Refresh List");
        JButton exitBtn = createExitButton("Exit");
        
        refreshBtn.addActionListener(e -> loadVehicles());
        exitBtn.addActionListener(e -> System.exit(0));
        
        panel.add(refreshBtn);
        panel.add(exitBtn);
        
        return panel;
    }
    
    private JButton createPrimaryButton(String text) {
        RoundedButton button = new RoundedButton(text, 12);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(11, 111, 175)); // #0B6FAF
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 48, 40)); // 24px horizontal padding each side
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(9, 74, 122)); // Hover: #094A7A
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(11, 111, 175));
                button.repaint();
            }
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(6, 53, 87)); // Pressed: #063557
                button.repaint();
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(9, 74, 122));
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        RoundedButton button = new RoundedButton(text, 12);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Semibold 13
        button.setBackground(Color.WHITE); // #FFFFFF
        button.setForeground(new Color(11, 111, 175)); // Text: #0B6FAF
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 48, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorderColor(new Color(215, 222, 230)); // Border: 1px solid #D7DEE6
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(238, 247, 255)); // Hover: #EEF7FF
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.repaint();
            }
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(217, 237, 255)); // Pressed: #D9EDFF
                button.repaint();
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(238, 247, 255));
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JButton createExitButton(String text) {
        RoundedButton button = new RoundedButton(text, 12);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(231, 76, 60)); // #E74C3C
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 48, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43)); // Hover: #C0392B
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(231, 76, 60));
                button.repaint();
            }
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(146, 43, 33)); // Pressed: #922B21
                button.repaint();
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
                button.repaint();
            }
        });
        
        return button;
    }
    
    // Custom RoundedButton class for rounded corners
    class RoundedButton extends JButton {
        private int cornerRadius;
        private Color borderColor;
        
        public RoundedButton(String text, int radius) {
            super(text);
            this.cornerRadius = radius;
            this.borderColor = null;
        }
        
        public void setBorderColor(Color color) {
            this.borderColor = color;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            // Paint border if specified
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
            
            g2.dispose();
            
            // Paint text
            super.paintComponent(g);
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            // Custom border painting handled in paintComponent
        }
    }
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Body: 14pt Regular
        textField.setBackground(Color.WHITE); // Background #FFFFFF
        textField.setForeground(new Color(34, 40, 49)); // Primary Text #222831
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 8, true), // Border #D7DEE6, 8px radius
            BorderFactory.createEmptyBorder(8, 12, 8, 12) // Internal padding
        ));
        textField.setCaretColor(new Color(11, 111, 175)); // Primary Blue caret
        
        // Placeholder text effect
        textField.setForeground(new Color(156, 163, 175)); // Placeholder color #9CA3AF
        textField.setText("Enter registration number...");
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals("Enter registration number...")) {
                    textField.setText("");
                    textField.setForeground(new Color(34, 40, 49)); // Primary Text
                }
                // Focus border with 2px solid #00B0D8 outline (accessibility)
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 176, 216), 2, true), // 2px solid Accent Cyan #00B0D8
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(new Color(156, 163, 175)); // Placeholder color
                    textField.setText("Enter registration number...");
                }
                // Reset to normal border
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(215, 222, 230), 8, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return textField;
    }
    
    private void loadVehicles() {
        // Create loading dialog
        JDialog loadingDialog = new JDialog(this, "Loading", true);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setUndecorated(false);
        
        JPanel loadingPanel = new JPanel(new BorderLayout(0, 16)); // 16px spacing (2 units)
        loadingPanel.setBorder(new EmptyBorder(24, 24, 24, 24)); // 24px padding (3 units)
        
        JLabel loadingLabel = new JLabel("Loading vehicles, please wait...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Body: 14pt Regular
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        loadingPanel.add(loadingLabel, BorderLayout.CENTER);
        loadingPanel.add(progressBar, BorderLayout.SOUTH);
        
        loadingDialog.add(loadingPanel);
        
        // Load vehicles in background thread
        SwingWorker<List<Vehicle>, Void> worker = new SwingWorker<List<Vehicle>, Void>() {
            @Override
            protected List<Vehicle> doInBackground() throws Exception {
                return vehicleService.getAllVehicles();
            }
            
            @Override
            protected void done() {
                try {
                    List<Vehicle> vehicles = get();
                    tableModel.setRowCount(0);
                    
                    // Load availability from vehicles.csv
                    java.util.Map<String, String> availabilityMap = loadAvailabilityFromCSV();
                    
                    for (Vehicle v : vehicles) {
                        String type = v.getClass().getSimpleName();
                        String model = getVehicleModel(v);
                        String regNumber = v.getRegisterNumber();
                        String availability = availabilityMap.getOrDefault(regNumber, "Available");
                        
                        tableModel.addRow(new Object[]{
                            type,
                            model,
                            availability
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    loadingDialog.dispose();
                }
            }
        };
        
        worker.execute();
        loadingDialog.setVisible(true);
    }
    
    private java.util.Map<String, String> loadAvailabilityFromCSV() {
        java.util.Map<String, String> availabilityMap = new java.util.HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line = br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                
                if (parts.length >= 9) {
                    String regNumber = parts[0].trim();
                    String availability = parts[8].trim();
                    availabilityMap.put(regNumber, availability.isEmpty() ? "Available" : availability);
                } else if (parts.length >= 1) {
                    String regNumber = parts[0].trim();
                    availabilityMap.put(regNumber, "Available");
                }
            }
        } catch (IOException e) {
            // If file doesn't exist or error reading, return empty map
            System.err.println("Error loading availability from CSV: " + e.getMessage());
        }
        
        return availabilityMap;
    }
    
    private String getVehicleModel(Vehicle v) {
        if (v instanceof Car) return ((Car) v).getModel();
        if (v instanceof Van) return ((Van) v).getModel();
        if (v instanceof Bike) return ((Bike) v).getModel();
        if (v instanceof Lorry) return ((Lorry) v).getModel();
        if (v instanceof Bus) return ((Bus) v).getModel();
        return "Unknown";
    }
    
    private Vehicle findVehicleByTypeAndModel(String type, String model) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        for (Vehicle v : vehicles) {
            if (v.getClass().getSimpleName().equals(type) && getVehicleModel(v).equals(model)) {
                return v;
            }
        }
        return null;
    }
    
    private void showDefaultLogo() {
        rightPanel.removeAll();
        
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        // Load logo from photos folder
        File logoFile = new File("data/photos/logo.png");
        JLabel logoLabel;
        
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
            Image logoImg = logoIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(logoImg));
        } else {
            // Fallback to emoji if logo not found
            logoLabel = new JLabel("🚗");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 150));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel companyLabel = new JLabel("DriveSmart.pvt Limited");
        companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // H2: 18pt Bold
        companyLabel.setForeground(new Color(11, 111, 175)); // Primary Blue #0B6FAF
        companyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(logoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 24))); // 24px spacing (3 units)
        contentPanel.add(companyLabel);
        
        logoPanel.add(contentPanel);
        rightPanel.add(logoPanel, BorderLayout.CENTER);
        
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    
    private void showVehicleDetails() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        String model = (String) tableModel.getValueAt(selectedRow, 1);
        String type = (String) tableModel.getValueAt(selectedRow, 0);
        String availability = (String) tableModel.getValueAt(selectedRow, 2);
        Vehicle vehicle = findVehicleByTypeAndModel(type, model);
        
        if (vehicle == null) return;
        
        // Merge both panels into one combined view
        rightPanel.removeAll();
        
        // Create a single combined panel with details and photos
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.setBackground(Color.WHITE);
        combinedPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(215, 222, 230), 1, true), // 1px solid border #D7DEE6, 6px radius
            BorderFactory.createEmptyBorder(16, 16, 16, 16) // 16px internal padding
        ));
        
        // Create table for vehicle information
        String[][] tableData;
        String[] columnNames = {"Property", "Value"};
        
        if (vehicle instanceof Car) {
            Car car = (Car) vehicle;
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Model", car.getModel()},
                {"Color", vehicle.getColor()},
                {"Number of Doors", String.valueOf(car.getNumberOfDoors())}
            };
        } else if (vehicle instanceof Van) {
            Van van = (Van) vehicle;
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Model", van.getModel()},
                {"Color", vehicle.getColor()},
                {"Seating Capacity", String.valueOf(van.getSeatingCapacity())}
            };
        } else if (vehicle instanceof Bike) {
            Bike bike = (Bike) vehicle;
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Model", bike.getModel()},
                {"Color", vehicle.getColor()}
            };
        } else if (vehicle instanceof Lorry) {
            Lorry lorry = (Lorry) vehicle;
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Model", lorry.getModel()},
                {"Color", vehicle.getColor()},
                {"Load Capacity", lorry.getLoadCapacity() + " kg"}
            };
        } else if (vehicle instanceof Bus) {
            Bus bus = (Bus) vehicle;
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Model", bus.getModel()},
                {"Color", vehicle.getColor()},
                {"Seating Capacity", String.valueOf(bus.getCapacity())}
            };
        } else {
            tableData = new String[][] {
                {"Registration Number", vehicle.getRegisterNumber()},
                {"Type", vehicle.getClass().getSimpleName()},
                {"Color", vehicle.getColor()}
            };
        }
        
        // Create table with no border
        JTable infoTable = new JTable(tableData, columnNames);
        infoTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoTable.setRowHeight(32);
        infoTable.setShowGrid(false);
        infoTable.setIntercellSpacing(new Dimension(0, 0));
        infoTable.setBorder(BorderFactory.createEmptyBorder());
        infoTable.setBackground(Color.WHITE);
        infoTable.setEnabled(false);
        
        // Style table header
        infoTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTable.getTableHeader().setBackground(Color.WHITE);
        infoTable.getTableHeader().setForeground(new Color(34, 40, 49));
        infoTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        infoTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer for clean look
        infoTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                label.setBackground(Color.WHITE);
                if (column == 0) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    label.setForeground(new Color(107, 114, 128)); // Secondary Text
                } else {
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    label.setForeground(new Color(34, 40, 49)); // Primary Text
                }
                return label;
            }
        });
        
        // Add table title with styled header
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(215, 222, 230)), // 1px solid bottom border #D7DEE6
            BorderFactory.createEmptyBorder(8, 8, 8, 0) // 8px top, 8px left padding
        ));
        
        JLabel titleLabel = new JLabel("<html><span style='letter-spacing: 1px;'>VEHICLE INFORMATION</span></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bold, 18pt
        titleLabel.setForeground(new Color(11, 111, 175)); // DriveSmart Blue #0B6FAF
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create Select button panel (to be placed at bottom)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 16));
        buttonPanel.setBackground(Color.WHITE);
        JButton selectButton = createPrimaryButton("Select");
        selectButton.setPreferredSize(new Dimension(140, 40));
        
        // Check availability and disable button if not available
        if (availability.equalsIgnoreCase("Rented") || availability.equalsIgnoreCase("Maintenance") || 
            availability.equalsIgnoreCase("Booked") || availability.equalsIgnoreCase("Maintaining")) {
            selectButton.setEnabled(false);
            selectButton.setBackground(new Color(180, 180, 180));
            selectButton.setToolTipText("Vehicle is currently " + availability);
        } else {
            selectButton.addActionListener(e -> {
                showCustomerDetailsDialog(vehicle);
            });
        }
        
        buttonPanel.add(selectButton);
        
        // Create white card for vehicle information section
        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBackground(new Color(255, 255, 255)); // Background: #FFFFFF
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(new Color(215, 222, 230), 1, true), // 1px solid border #D7DEE6, 6px radius
                BorderFactory.createLineBorder(new Color(13, 26, 38, 20), 2) // Shadow: rgba(13, 26, 38, 0.08) at 0px 2px 4px
            ),
            BorderFactory.createEmptyBorder(20, 20, 20, 20) // Internal padding: 20px
        ));
        infoCard.add(titlePanel, BorderLayout.NORTH);
        infoCard.add(infoTable, BorderLayout.CENTER);
        
        // Add description section if description exists
        String description = vehicle.getDescription();
        if (description != null && !description.isEmpty()) {
            JPanel descPanel = new JPanel(new BorderLayout(0, 8));
            descPanel.setBackground(Color.WHITE);
            descPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0)); // Top margin
            
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            descLabel.setForeground(new Color(107, 114, 128)); // Secondary Text
            descPanel.add(descLabel, BorderLayout.NORTH);
            
            JTextArea descArea = new JTextArea(description);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descArea.setForeground(new Color(34, 40, 49)); // Primary Text
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(new Color(249, 250, 251)); // Light gray background
            descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            descPanel.add(descArea, BorderLayout.CENTER);
            
            infoCard.add(descPanel, BorderLayout.SOUTH);
        } else {
            infoCard.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        // Move button panel below description or table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        if (description != null && !description.isEmpty()) {
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        }
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Add bottom panel to infoCard
        if (description != null && !description.isEmpty()) {
            JPanel centerWithDesc = new JPanel(new BorderLayout());
            centerWithDesc.setBackground(Color.WHITE);
            centerWithDesc.add(infoTable, BorderLayout.NORTH);
            
            JPanel descSection = new JPanel(new BorderLayout(0, 8));
            descSection.setBackground(Color.WHITE);
            descSection.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
            
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            descLabel.setForeground(new Color(107, 114, 128));
            descSection.add(descLabel, BorderLayout.NORTH);
            
            JTextArea descArea = new JTextArea(description);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descArea.setForeground(new Color(34, 40, 49));
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setEditable(false);
            descArea.setBackground(new Color(249, 250, 251));
            descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            descSection.add(descArea, BorderLayout.CENTER);
            
            centerWithDesc.add(descSection, BorderLayout.CENTER);
            
            infoCard.add(centerWithDesc, BorderLayout.CENTER);
            infoCard.add(bottomPanel, BorderLayout.SOUTH);
        } else {
            infoCard.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        // Add details at the top
        JPanel detailsSection = new JPanel(new BorderLayout());
        detailsSection.setBackground(new Color(244, 247, 250)); // Page background
        detailsSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0)); // 16px bottom margin
        detailsSection.add(infoCard, BorderLayout.CENTER);
        
        // Display photos
        displayPhotos(vehicle);
        
        // Add photos below details
        JPanel photosSection = new JPanel(new BorderLayout());
        photosSection.setBackground(Color.WHITE);
        
        JScrollPane photoScroll = new JScrollPane(photoPanel);
        photoScroll.setBorder(BorderFactory.createEmptyBorder());
        photoScroll.setBackground(Color.WHITE);
        photoScroll.getViewport().setBackground(Color.WHITE);
        photosSection.add(photoScroll, BorderLayout.CENTER);
        
        // Combine details and photos
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        contentArea.add(detailsSection, BorderLayout.NORTH);
        contentArea.add(photosSection, BorderLayout.CENTER);
        
        JScrollPane mainScroll = new JScrollPane(contentArea);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.setBackground(Color.WHITE);
        mainScroll.getViewport().setBackground(Color.WHITE);
        
        combinedPanel.add(mainScroll, BorderLayout.CENTER);
        rightPanel.add(combinedPanel, BorderLayout.CENTER);
        
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    
    private void displayPhotos(Vehicle vehicle) {
        photoPanel.removeAll();
        
        List<String> photoPaths = vehicle.getPhotoPaths();
        
        if (photoPaths.isEmpty()) {
            JLabel noPhotoLabel = new JLabel("No photos available");
            noPhotoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14)); // Body: 14pt Italic
            noPhotoLabel.setForeground(new Color(156, 163, 175)); // Disabled/Hint #9CA3AF
            photoPanel.add(noPhotoLabel);
        } else {
            for (String path : photoPaths) {
                // Handle both "photos/..." and "Data/photos/..." formats
                File imgFile;
                if (path.toLowerCase().startsWith("data/") || path.toLowerCase().startsWith("data\\")) {
                    imgFile = new File(path);
                } else {
                    imgFile = new File("data/" + path);
                }
                
                JPanel photoCard = new JPanel(new BorderLayout(0, 8)); // 8px spacing between image and caption
                photoCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(230, 237, 242), 1, true), // Photo card border #E6EDF2, rounded
                        BorderFactory.createLineBorder(new Color(0, 0, 0, 10), 2) // Slight shadow effect
                    ),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12) // 12px card padding
                ));
                photoCard.setPreferredSize(new Dimension(264, 240)); // 240+24px padding width, 180+60px height
                photoCard.setBackground(Color.WHITE); // Panel Background #FFFFFF
                
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    // Calculate dimensions to fit 236x176 (240-4 for padding, 180-4 for padding) while maintaining aspect ratio
                    int originalWidth = icon.getIconWidth();
                    int originalHeight = icon.getIconHeight();
                    int targetWidth = 236; // 240 - 4px for inner padding
                    int targetHeight = 176; // 180 - 4px for inner padding
                    
                    double widthRatio = (double) targetWidth / originalWidth;
                    double heightRatio = (double) targetHeight / originalHeight;
                    double ratio = Math.min(widthRatio, heightRatio);
                    
                    int scaledWidth = (int) (originalWidth * ratio);
                    int scaledHeight = (int) (originalHeight * ratio);
                    
                    Image img = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    
                    // Create panel for image with 2px white inner padding and 1px gray border
                    JPanel imgContainer = new JPanel(new GridBagLayout());
                    imgContainer.setBackground(Color.WHITE); // 2px white inner padding
                    imgContainer.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(215, 222, 230), 1), // 1px gray border
                        BorderFactory.createEmptyBorder(2, 2, 2, 2) // 2px white inner padding
                    ));
                    imgContainer.setPreferredSize(new Dimension(240, 180));
                    
                    JLabel imgLabel = new JLabel(new ImageIcon(img));
                    imgLabel.setHorizontalAlignment(JLabel.CENTER);
                    imgContainer.add(imgLabel);
                    
                    photoCard.add(imgContainer, BorderLayout.CENTER);
                } else {
                    // Show placeholder with dashed border
                    JLabel noImgLabel = new JLabel("Image not found");
                    noImgLabel.setHorizontalAlignment(JLabel.CENTER);
                    noImgLabel.setVerticalAlignment(JLabel.CENTER);
                    noImgLabel.setPreferredSize(new Dimension(240, 180));
                    noImgLabel.setForeground(new Color(156, 163, 175)); // Disabled/Hint #9CA3AF
                    noImgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    noImgLabel.setBorder(BorderFactory.createDashedBorder(new Color(208, 215, 221), 2.0f, 4.0f, 4.0f, true)); // Dashed border #D0D7DD
                    photoCard.add(noImgLabel, BorderLayout.CENTER);
                }
                
                photoPanel.add(photoCard);
            }
        }
        
        photoPanel.revalidate();
        photoPanel.repaint();
        
        // Refresh the right panel
        rightPanel.revalidate();
        rightPanel.repaint();
    }
    
    private void searchVehicle() {
        // Create custom search dialog with styled input field
        JDialog searchDialog = new JDialog(this, "Search Vehicle", true);
        searchDialog.setSize(450, 240);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setLayout(new BorderLayout(0, 16));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        contentPanel.setBackground(Color.WHITE);
        
        // Search Type dropdown
        JLabel typeLabel = new JLabel("Search By:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeLabel.setForeground(new Color(34, 40, 49));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] searchTypes = {"Registration Number", "Model", "Type"};
        JComboBox<String> searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchTypeCombo.setBackground(Color.WHITE);
        searchTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Search input
        JLabel promptLabel = new JLabel("Enter Search Term:");
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        promptLabel.setForeground(new Color(34, 40, 49));
        promptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField inputField = createStyledTextField();
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton searchButton = createPrimaryButton("Search");
        JButton cancelButton = createSecondaryButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);
        
        contentPanel.add(typeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(searchTypeCombo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        contentPanel.add(promptLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(inputField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        contentPanel.add(buttonPanel);
        
        searchDialog.add(contentPanel);
        
        searchButton.addActionListener(e -> {
            String searchTerm = inputField.getText();
            String searchType = (String) searchTypeCombo.getSelectedItem();
            searchDialog.dispose();
            performSearch(searchTerm, searchType);
        });
        
        cancelButton.addActionListener(e -> searchDialog.dispose());
        
        inputField.addActionListener(e -> searchButton.doClick());
        
        searchDialog.setVisible(true);
    }
    
    private void performSearch(String searchTerm, String searchType) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a search term!", 
                "Empty Search", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String term = searchTerm.trim().toLowerCase();
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        List<Integer> matchingRows = new ArrayList<>();
        
        // Search based on selected type
        for (int i = 0; i < allVehicles.size(); i++) {
            Vehicle vehicle = allVehicles.get(i);
            boolean matches = false;
            
            switch (searchType) {
                case "Registration Number":
                    matches = vehicle.getRegisterNumber().toLowerCase().contains(term);
                    break;
                    
                case "Model":
                    String model = getVehicleModel(vehicle).toLowerCase();
                    matches = model.contains(term);
                    break;
                    
                case "Type":
                    String type = vehicle.getClass().getSimpleName().toLowerCase();
                    matches = type.contains(term);
                    break;
            }
            
            if (matches) {
                matchingRows.add(i);
            }
        }
        
        // Display results
        if (matchingRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No vehicles found matching: \"" + searchTerm + "\"", 
                "No Results", 
                JOptionPane.INFORMATION_MESSAGE);
        } else if (matchingRows.size() == 1) {
            // Single result - select and show details
            int row = matchingRows.get(0);
            vehicleTable.setRowSelectionInterval(row, row);
            vehicleTable.scrollRectToVisible(vehicleTable.getCellRect(row, 0, true));
            showVehicleDetails();
        } else {
            // Multiple results - select first and show count
            vehicleTable.clearSelection();
            for (int row : matchingRows) {
                vehicleTable.addRowSelectionInterval(row, row);
            }
            vehicleTable.scrollRectToVisible(vehicleTable.getCellRect(matchingRows.get(0), 0, true));
            JOptionPane.showMessageDialog(this, 
                "Found " + matchingRows.size() + " matching vehicles.\nAll matches have been selected in the table.", 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void performHeaderSearch(String searchText) {
        if (searchText.isEmpty()) {
            // Show all vehicles if search is empty
            loadVehicles();
            return;
        }
        
        String term = searchText.toLowerCase();
        List<Vehicle> allVehicles = vehicleService.getAllVehicles();
        java.util.Map<String, String> availabilityMap = loadAvailabilityFromCSV();
        
        tableModel.setRowCount(0);
        
        for (Vehicle vehicle : allVehicles) {
            String type = vehicle.getClass().getSimpleName();
            String model = getVehicleModel(vehicle);
            String regNumber = vehicle.getRegisterNumber();
            String availability = availabilityMap.getOrDefault(regNumber, "Available");
            
            // Search in registration number, model, and type
            if (regNumber.toLowerCase().contains(term) || 
                model.toLowerCase().contains(term) || 
                type.toLowerCase().contains(term)) {
                
                tableModel.addRow(new Object[]{
                    type,
                    model,
                    availability
                });
            }
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Drive Smart - Vehicle Rental Management\n" +
            "Version 1.0\n\n" +
            "Developed with Java Swing\n" +
            "© 2025 NewSoft",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showCustomerDetailsDialog(Vehicle vehicle) {
        JDialog dialog = new JDialog(this, "Customer Details - " + getVehicleModel(vehicle), true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        contentPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Enter Customer Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(11, 111, 175));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Subtitle with vehicle info
        JLabel subtitleLabel = new JLabel("Vehicle: " + getVehicleModel(vehicle) + " (" + vehicle.getRegisterNumber() + ")");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        // Form fields panel
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setBackground(Color.WHITE);
        formFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Full Name
        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFullName.setForeground(new Color(34, 40, 49));
        JTextField txtFullName = createStyledInputField();
        txtFullName.setToolTipText("Enter customer's full name");
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formFieldsPanel.add(lblFullName, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtFullName, gbc);
        
        // NIC
        JLabel lblNIC = new JLabel("NIC Number:");
        lblNIC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNIC.setForeground(new Color(34, 40, 49));
        JTextField txtNIC = createStyledInputField();
        txtNIC.setToolTipText("Enter National Identity Card number");
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formFieldsPanel.add(lblNIC, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtNIC, gbc);
        
        // Phone
        JLabel lblPhone = new JLabel("Phone:");
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPhone.setForeground(new Color(34, 40, 49));
        JTextField txtPhone = createStyledInputField();
        txtPhone.setToolTipText("Enter phone number");
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formFieldsPanel.add(lblPhone, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtPhone, gbc);
        
        // Address
        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblAddress.setForeground(new Color(34, 40, 49));
        JTextField txtAddress = createStyledInputField();
        txtAddress.setToolTipText("Enter address");
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formFieldsPanel.add(lblAddress, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtAddress, gbc);
        
        // License Number
        JLabel lblLicense = new JLabel("License Number:");
        lblLicense.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLicense.setForeground(new Color(34, 40, 49));
        JTextField txtLicense = createStyledInputField();
        txtLicense.setToolTipText("Enter driver's license number");
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formFieldsPanel.add(lblLicense, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtLicense, gbc);
        
        // Email
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEmail.setForeground(new Color(34, 40, 49));
        JTextField txtEmail = createStyledInputField();
        txtEmail.setToolTipText("Enter email address");
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        formFieldsPanel.add(lblEmail, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFieldsPanel.add(txtEmail, gbc);
        
        contentPanel.add(formFieldsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton confirmButton = createPrimaryButton("Confirm");
        JButton cancelButton = createSecondaryButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            // Validate fields
            String fullName = txtFullName.getText().trim();
            String nic = txtNIC.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String license = txtLicense.getText().trim();
            String email = txtEmail.getText().trim();
            
            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Full Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtFullName.requestFocus();
                return;
            }
            if (nic.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "NIC Number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtNIC.requestFocus();
                return;
            }
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Phone is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtPhone.requestFocus();
                return;
            }
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Address is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtAddress.requestFocus();
                return;
            }
            if (license.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "License Number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtLicense.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Email is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
            
            // Save customer data to CSV
            if (saveCustomerDataToCSV(fullName, nic, phone, address, license, email, vehicle)) {
                // Update vehicle availability to "Booked"
                updateVehicleAvailabilityInCSV(vehicle.getRegisterNumber(), "Booked");
                
                // Reload vehicle list to reflect the updated status
                loadVehicles();
                
                // Success message
                JOptionPane.showMessageDialog(dialog,
                    "Customer Details Submitted Successfully!\n\n" +
                    "Name: " + fullName + "\n" +
                    "NIC: " + nic + "\n" +
                    "Phone: " + phone + "\n" +
                    "License: " + license + "\n" +
                    "Email: " + email + "\n\n" +
                    "Vehicle: " + getVehicleModel(vehicle) + " (" + vehicle.getRegisterNumber() + ")\n\n" +
                    "Status: Booked",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Failed to save customer data. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        contentPanel.add(buttonPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    private JTextField createStyledInputField() {
        JTextField textField = new JTextField(25);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBackground(Color.WHITE);
        textField.setForeground(new Color(34, 40, 49));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        textField.setCaretColor(new Color(11, 111, 175));
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 176, 216), 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
        });
        
        return textField;
    }
    
    private boolean saveCustomerDataToCSV(String fullName, String nic, String phone, String address, 
                                          String license, String email, Vehicle vehicle) {
        try {
            File csvFile = new File("Data/Customerdata.csv");
            boolean fileExists = csvFile.exists();
            
            // Create parent directory if it doesn't exist
            if (!csvFile.getParentFile().exists()) {
                csvFile.getParentFile().mkdirs();
            }
            
            // Use FileWriter with append mode
            FileWriter fw = new FileWriter(csvFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Write header if file is new or empty
            if (!fileExists || csvFile.length() == 0) {
                bw.write("Full Name,NIC,Phone,Address,License Number,Email,Vehicle Model,Vehicle Registration,Date Time");
                bw.newLine();
            }
            
            // Get current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);
            
            // Write customer data
            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                escapeCsv(fullName),
                escapeCsv(nic),
                escapeCsv(phone),
                escapeCsv(address),
                escapeCsv(license),
                escapeCsv(email),
                escapeCsv(getVehicleModel(vehicle)),
                escapeCsv(vehicle.getRegisterNumber()),
                dateTime
            );
            
            bw.write(line);
            bw.newLine();
            bw.close();
            fw.close();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private void updateVehicleAvailabilityInCSV(String regNumber, String newAvailability) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(regNumber + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 9) {
                        // Update availability (column 8, index 8)
                        parts[8] = newAvailability;
                        line = String.join(",", parts);
                    } else if (parts.length == 8) {
                        // Add availability if it doesn't exist
                        line = line + "," + newAvailability;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating vehicle availability: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicleLoadCapacityInCSV(String regNumber, String newLoad) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(regNumber + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 7) {
                        // Update load capacity (column index 6)
                        parts[6] = newLoad;
                        line = String.join(",", parts);
                    } else {
                        // Pad missing columns up to load capacity index
                        java.util.List<String> partsList = new ArrayList<>(java.util.Arrays.asList(parts));
                        while (partsList.size() < 7) partsList.add("");
                        partsList.set(6, newLoad);
                        line = String.join(",", partsList);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String ln : lines) {
                bw.write(ln);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating vehicle load capacity: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show welcome screen first
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setVisible(true);
        });
    }
}

// New Welcome Screen Class
class WelcomeScreen extends JFrame {
    
    public WelcomeScreen() {
        setTitle("Drive Smart - Vehicle Rental System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Logo
        File logoFile = new File("data/photos/logo.png");
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
            Image logoImg = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            gbc.gridy = 0;
            mainPanel.add(logoLabel, gbc);
        } else {
            // Fallback emoji logo
            JLabel logoLabel = new JLabel("🚗");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            gbc.gridy = 0;
            mainPanel.add(logoLabel, gbc);
        }
        
        // Welcome title
        JLabel titleLabel = new JLabel("Welcome to Drive Smart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(11, 111, 175));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Vehicle Rental Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 40, 0);
        mainPanel.add(subtitleLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Customer button
        JButton customerButton = createStyledButton("Customer", new Color(11, 111, 175));
        customerButton.addActionListener(e -> {
            dispose();
            VehicleRentalUI frame = new VehicleRentalUI();
            frame.setVisible(true);
        });
        
        // Company button
        JButton companyButton = createStyledButton("Company", new Color(11, 111, 175));
        companyButton.addActionListener(e -> {
            showCompanyLogin();
        });
        
        buttonPanel.add(customerButton);
        buttonPanel.add(companyButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Custom border painting handled in paintComponent
            }
        };
        
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(180, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(9, 74, 122));
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.repaint();
            }
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(6, 53, 87));
                button.repaint();
            }
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(9, 74, 122));
                button.repaint();
            }
        });
        
        return button;
    }
    
    private void showCompanyLogin() {
        JDialog loginDialog = new JDialog(this, "Company Login", true);
        loginDialog.setSize(800, 550);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new BorderLayout());
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Company Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(11, 111, 175));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Username field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsername.setForeground(new Color(34, 40, 49));
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setForeground(new Color(34, 40, 49));
        txtUsername.setMaximumSize(new Dimension(400, 40));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        txtUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(lblUsername);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(txtUsername);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        
        // Password field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setForeground(new Color(34, 40, 49));
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(new Color(34, 40, 49));
        txtPassword.setMaximumSize(new Dimension(400, 40));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(lblPassword);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(txtPassword);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 24)));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton loginButton = createStyledButton("Login", new Color(11, 111, 175));
        loginButton.setPreferredSize(new Dimension(120, 40));
        
        JButton cancelButton = createStyledButton("Cancel", new Color(107, 114, 128));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        
        loginButton.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            
            // Verify credentials (default: admin/admin)
            if (username.equals("admin") && password.equals("admin")) {
                loginDialog.dispose();
                showCompanyDashboard();
            } else {
                JOptionPane.showMessageDialog(loginDialog,
                    "Invalid username or password.\n\nDefault credentials:\nUsername: admin\nPassword: admin",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        });
        
        cancelButton.addActionListener(e -> loginDialog.dispose());
        
        // Add Enter key support
        txtPassword.addActionListener(e -> loginButton.doClick());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel);
        
        loginDialog.add(contentPanel, BorderLayout.CENTER);
        loginDialog.setVisible(true);
    }
    
    private void showCompanyDashboard() {
        JFrame dashboardFrame = new JFrame("Drive Smart - Company Dashboard");
        dashboardFrame.setSize(900, 650);
        dashboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboardFrame.setLocationRelativeTo(this);
        dashboardFrame.setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        
        // Logo
        File logoFile = new File("data/photos/logo.png");
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
            Image logoImg = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            gbc.gridy = 0;
            mainPanel.add(logoLabel, gbc);
        } else {
            JLabel logoLabel = new JLabel("🚗");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            gbc.gridy = 0;
            mainPanel.add(logoLabel, gbc);
        }
        
        // Dashboard title
        JLabel titleLabel = new JLabel("Company Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(11, 111, 175));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 1;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage your vehicle rental business");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 40, 0);
        mainPanel.add(subtitleLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        // Payments button
        JButton paymentsButton = createStyledButton("Payments", new Color(11, 111, 175));
        paymentsButton.addActionListener(e -> {
            // Open Payments UI dialog (embedded panel)
            try {
                main.java.com.newsoft.VehicleRenting.payments.PaymentsUI paymentsUI =
                    new main.java.com.newsoft.VehicleRenting.payments.PaymentsUI();

                JDialog dlg = new JDialog(dashboardFrame, "Payments", true);
                dlg.setContentPane(paymentsUI.getPaymentPanel());
                dlg.pack();
                dlg.setLocationRelativeTo(dashboardFrame);
                dlg.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dashboardFrame,
                    "Error opening Payments UI: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Manage Vehicles button
        JButton manageVehiclesButton = createStyledButton("Manage Vehicles", new Color(11, 111, 175));
        manageVehiclesButton.addActionListener(e -> {
            showManageVehiclesInterface(dashboardFrame);
        });
        
        buttonPanel.add(paymentsButton);
        buttonPanel.add(manageVehiclesButton);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(buttonPanel, gbc);
        
        // Logout button at bottom
        JButton logoutButton = createStyledButton("Logout", new Color(231, 76, 60));
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.addActionListener(e -> {
            dashboardFrame.dispose();
        });
        
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(logoutButton, gbc);
        
        dashboardFrame.add(mainPanel, BorderLayout.CENTER);
        dashboardFrame.setVisible(true);
    }
    
    private void showManageVehiclesInterface(JFrame parentFrame) {
        JFrame manageFrame = new JFrame("Drive Smart - Manage Vehicles");
        manageFrame.setSize(1000, 600);
        manageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        manageFrame.setLocationRelativeTo(parentFrame);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(244, 247, 250));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(11, 111, 175));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Vehicle Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table to display vehicles
        String[] columnNames = {"Reg Number", "Color", "Type", "Model", "Doors", "Seats", "Load (kg)", "Photos", "Availability"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6 || column == 8; // Seats, Load, and Availability columns are editable
            }
        };
        
        JTable vehicleTable = new JTable(tableModel);
        vehicleTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vehicleTable.setRowHeight(35);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setShowGrid(true);
        vehicleTable.setGridColor(new Color(215, 222, 230));
        vehicleTable.setBackground(Color.WHITE);
        vehicleTable.setSelectionBackground(new Color(230, 240, 250));
        vehicleTable.setSelectionForeground(new Color(34, 40, 49));
        
        // Add combo box editor for Availability column
        String[] availabilityOptions = {"Available", "Booked", "Maintaining"};
        JComboBox<String> availabilityCombo = new JComboBox<>(availabilityOptions);
        availabilityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        vehicleTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(availabilityCombo));
        
        // Add listener to save changes to CSV when columns are updated
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                String regNumber = (String) tableModel.getValueAt(row, 0);
                
                if (column == 8) {
                    // Availability column updated
                    String newAvailability = (String) tableModel.getValueAt(row, 8);
                    updateVehicleAvailabilityInCSV(regNumber, newAvailability);
                } else if (column == 5) {
                    // Seats column updated
                    String newSeats = String.valueOf(tableModel.getValueAt(row, 5));
                    if (newSeats.equals("-") || newSeats.trim().isEmpty()) {
                        newSeats = "";
                    }
                    final String finalSeats = newSeats;
                    final int finalRow = row;
                    updateVehicleSeatingCapacityInCSV(regNumber, finalSeats);
                    // Refresh this row to show the updated value
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt(finalSeats.isEmpty() ? "-" : finalSeats, finalRow, 5);
                    });
                } else if (column == 6) {
                    // Load column updated
                    String newLoad = String.valueOf(tableModel.getValueAt(row, 6));
                    if (newLoad.equals("-") || newLoad.trim().isEmpty()) {
                        newLoad = "";
                    }
                    final String finalLoad = newLoad;
                    final int finalRow = row;
                    updateVehicleLoadCapacityInCSV(regNumber, finalLoad);
                    // Refresh this row to show the updated value
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt(finalLoad.isEmpty() ? "-" : finalLoad, finalRow, 6);
                    });
                }
            }
        });
        
        // Style table header
        vehicleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        vehicleTable.getTableHeader().setBackground(new Color(11, 111, 175));
        vehicleTable.getTableHeader().setForeground(Color.BLUE);
        vehicleTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Load vehicles from CSV
        List<Object[]> allVehiclesData = new ArrayList<>();
        loadVehiclesFromCSV(tableModel, allVehiclesData);
        
        // Add search functionality to title panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(new Color(11, 111, 175));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setForeground(Color.WHITE);
        searchPanel.add(searchLabel);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 32));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        searchField.setToolTipText("Enter registration number");
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                filterVehicleTable(tableModel, allVehiclesData, searchText);
            }
        });
        
        searchPanel.add(searchField);
        titlePanel.add(searchPanel, BorderLayout.EAST);
        
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(215, 222, 230), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(244, 247, 250));
        
        JButton addButton = createStyledButton("Add Vehicle", new Color(34, 197, 94));
        addButton.setPreferredSize(new Dimension(140, 40));
        addButton.addActionListener(e -> {
            showAddVehicleDialog(manageFrame, tableModel, allVehiclesData, searchField);
        });
        
        JButton deleteButton = createStyledButton("Delete", new Color(239, 68, 68));
        deleteButton.setPreferredSize(new Dimension(120, 40));
        deleteButton.addActionListener(e -> {
            int selectedRow = vehicleTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(manageFrame,
                    "Please select a vehicle to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String regNumber = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(manageFrame,
                "Are you sure you want to delete vehicle: " + regNumber + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                deleteVehicleFromCSV(regNumber);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(manageFrame,
                    "Vehicle deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton refreshButton = createStyledButton("Refresh", new Color(11, 111, 175));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            allVehiclesData.clear();
            loadVehiclesFromCSV(tableModel, allVehiclesData);
            searchField.setText("");
        });
        
        JButton closeButton = createStyledButton("Close", new Color(107, 114, 128));
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.addActionListener(e -> manageFrame.dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        manageFrame.add(mainPanel);
        manageFrame.setVisible(true);
    }
    
    private void loadVehiclesFromCSV(DefaultTableModel tableModel, List<Object[]> allVehiclesData) {
        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line = br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                
                if (parts.length >= 3) {
                    String regNumber = parts[0].trim();
                    String color = parts[1].trim();
                    String type = parts[2].trim();
                    String model = parts.length > 3 ? parts[3].trim() : "";
                    String doors = parts.length > 4 ? parts[4].trim() : "";
                    String seats = parts.length > 5 ? parts[5].trim() : "";
                    String load = parts.length > 6 ? parts[6].trim() : "";
                    String photos = parts.length > 7 ? parts[7].trim() : "";
                    String availability = parts.length > 8 ? parts[8].trim() : "Available";
                    
                    // Count photos
                    int photoCount = photos.isEmpty() ? 0 : photos.split("\\|").length;
                    String photoInfo = photoCount > 0 ? photoCount + " photo(s)" : "No photos";
                    
                    Object[] rowData = new Object[]{
                        regNumber,
                        color,
                        type,
                        model,
                        doors.isEmpty() ? "-" : doors,
                        seats.isEmpty() ? "-" : seats,
                        load.isEmpty() ? "-" : load,
                        photoInfo,
                        availability.isEmpty() ? "Available" : availability
                    };
                    
                    allVehiclesData.add(rowData);
                    tableModel.addRow(rowData);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error loading vehicles from CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterVehicleTable(DefaultTableModel tableModel, List<Object[]> allVehiclesData, String searchText) {
        tableModel.setRowCount(0);
        
        if (searchText.isEmpty()) {
            for (Object[] rowData : allVehiclesData) {
                tableModel.addRow(rowData);
            }
        } else {
            for (Object[] rowData : allVehiclesData) {
                String regNumber = ((String) rowData[0]).toLowerCase();
                if (regNumber.contains(searchText)) {
                    tableModel.addRow(rowData);
                }
            }
        }
    }
    
    private JTextField createStyledInputField(String placeholder) {
        JTextField textField = new JTextField(25);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBackground(Color.WHITE);
        textField.setForeground(new Color(34, 40, 49));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        textField.setCaretColor(new Color(11, 111, 175));
        textField.setToolTipText(placeholder);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 176, 216), 2),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                ));
            }
        });
        
        return textField;
    }
    
    private void showAddVehicleDialog(JFrame parentFrame, DefaultTableModel tableModel, 
                                       List<Object[]> allVehiclesData, JTextField searchField) {
        JDialog dialog = new JDialog(parentFrame, "Add New Vehicle", true);
        dialog.setSize(700, 750);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Add New Vehicle");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(34, 41, 49));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Registration Number
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel regLabel = new JLabel("Registration Number:");
        regLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(regLabel, gbc);
        
        gbc.gridx = 1;
        JTextField regField = createStyledInputField("e.g., CAR-004");
        formPanel.add(regField, gbc);
        
        // Color
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(colorLabel, gbc);
        
        gbc.gridx = 1;
        JTextField colorField = createStyledInputField("e.g., White");
        formPanel.add(colorField, gbc);
        
        // Type
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        String[] types = {"Car", "Van", "Bike", "Lorry", "Bus"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeCombo.setPreferredSize(new Dimension(300, 35));
        formPanel.add(typeCombo, gbc);
        
        // Model
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(modelLabel, gbc);
        
        gbc.gridx = 1;
        JTextField modelField = createStyledInputField("e.g., Toyota Corolla");
        formPanel.add(modelField, gbc);
        
        // Number of Doors (for Car)
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel doorsLabel = new JLabel("Number of Doors:");
        doorsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(doorsLabel, gbc);
        
        gbc.gridx = 1;
        JTextField doorsField = createStyledInputField("For Cars only");
        formPanel.add(doorsField, gbc);
        
        // Seating Capacity (for Van)
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel seatsLabel = new JLabel("Seating Capacity:");
        seatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(seatsLabel, gbc);
        
        gbc.gridx = 1;
        JTextField seatsField = createStyledInputField("For Vans only");
        formPanel.add(seatsField, gbc);
        
        // Load Capacity (for Lorry)
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel loadLabel = new JLabel("Load Capacity (kg):");
        loadLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(loadLabel, gbc);
        
        gbc.gridx = 1;
        JTextField loadField = createStyledInputField("For Lorries only");
        formPanel.add(loadField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setVerticalAlignment(JLabel.TOP);
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(4, 20);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setPreferredSize(new Dimension(300, 90));
        formPanel.add(descScrollPane, gbc);
        
        // Availability
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel availabilityLabel = new JLabel("Availability:");
        availabilityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(availabilityLabel, gbc);
        
        gbc.gridx = 1;
        String[] availabilityOptions = {"Available", "Rented", "Maintenance"};
        JComboBox<String> availabilityCombo = new JComboBox<>(availabilityOptions);
        availabilityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        availabilityCombo.setPreferredSize(new Dimension(300, 35));
        formPanel.add(availabilityCombo, gbc);
        
        // Photos
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel photosLabel = new JLabel("Photos:");
        photosLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(photosLabel, gbc);
        
        gbc.gridx = 1;
        JPanel photoPanel = new JPanel(new BorderLayout(5, 0));
        photoPanel.setBackground(Color.WHITE);
        
        JTextField photoPathField = new JTextField();
        photoPathField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        photoPathField.setEditable(false);
        photoPathField.setBackground(new Color(245, 245, 245));
        photoPathField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 222, 230), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        List<String> selectedPhotoPaths = new ArrayList<>();
        
        JButton browseButton = new JButton("Browse");
        browseButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        browseButton.setBackground(new Color(11, 111, 175));
        browseButton.setForeground(Color.WHITE);
        browseButton.setFocusPainted(false);
        browseButton.setBorderPainted(false);
        browseButton.setPreferredSize(new Dimension(90, 35));
        browseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        browseButton.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            // Add image file filters
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                           name.endsWith(".png") || name.endsWith(".gif") || 
                           name.endsWith(".bmp");
                }
                
                @Override
                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
                }
            });
            
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                selectedPhotoPaths.clear();
                
                for (File file : files) {
                    selectedPhotoPaths.add(file.getAbsolutePath());
                }
                
                if (selectedPhotoPaths.size() > 0) {
                    photoPathField.setText(selectedPhotoPaths.size() + " photo(s) selected");
                } else {
                    photoPathField.setText("");
                }
            }
        });
        
        photoPanel.add(photoPathField, BorderLayout.CENTER);
        photoPanel.add(browseButton, BorderLayout.EAST);
        formPanel.add(photoPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = createStyledButton("Save", new Color(34, 197, 94));
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.addActionListener(e -> {
            String regNumber = regField.getText().trim();
            String color = colorField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String model = modelField.getText().trim();
            
            if (regNumber.isEmpty() || color.isEmpty() || model.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all required fields (Registration, Color, Model).",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String doors = doorsField.getText().trim();
            String seats = seatsField.getText().trim();
            String load = loadField.getText().trim();
            String description = descArea.getText().trim();
            String availability = (String) availabilityCombo.getSelectedItem();
            
            // Copy photos to Data/photos directory
            String photoPathsString = "";
            if (!selectedPhotoPaths.isEmpty()) {
                List<String> copiedPaths = copyPhotosToDirectory(selectedPhotoPaths, regNumber);
                if (!copiedPaths.isEmpty()) {
                    photoPathsString = String.join("|", copiedPaths);
                }
            }
            
            addVehicleToCSV(regNumber, color, type, model, doors, seats, load, photoPathsString, availability, description);
            tableModel.setRowCount(0);
            allVehiclesData.clear();
            loadVehiclesFromCSV(tableModel, allVehiclesData);
            searchField.setText("");
            
            JOptionPane.showMessageDialog(dialog,
                "Vehicle added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        JButton cancelButton = createStyledButton("Cancel", new Color(107, 114, 128));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private List<String> copyPhotosToDirectory(List<String> sourcePaths, String regNumber) {
        List<String> copiedPaths = new ArrayList<>();
        File photoDir = new File("Data/photos");
        
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        
        for (int i = 0; i < sourcePaths.size(); i++) {
            try {
                File sourceFile = new File(sourcePaths.get(i));
                String extension = "";
                String fileName = sourceFile.getName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    extension = fileName.substring(dotIndex);
                }
                
                String newFileName = regNumber.replace("-", "_") + "_" + (i + 1) + extension;
                File destFile = new File(photoDir, newFileName);
                
                // Copy file
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                copiedPaths.add("Data/photos/" + newFileName);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                    "Error copying photo: " + e.getMessage(),
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        
        return copiedPaths;
    }
    
    private void addVehicleToCSV(String regNumber, String color, String type, String model,
                                  String doors, String seats, String load, String photoPaths, String availability, String description) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv", true))) {
            StringBuilder line = new StringBuilder();
            line.append(regNumber).append(",");
            line.append(color).append(",");
            line.append(type).append(",");
            line.append(model).append(",");
            
            // CSV format: regNumber,color,type,model,numberOfDoors,seatingCapacity,loadCapacity,photoPaths,availability
            // Add fields in correct order based on vehicle type
            if (type.equals("Car")) {
                line.append(doors.isEmpty() ? "" : doors).append(",");  // numberOfDoors
                line.append(",");  // seatingCapacity (empty for cars)
                line.append(",");  // loadCapacity (empty for cars)
            } else if (type.equals("Van")) {
                line.append(",");  // numberOfDoors (empty for vans)
                line.append(seats.isEmpty() ? "" : seats).append(",");  // seatingCapacity
                line.append(",");  // loadCapacity (empty for vans)
            } else if (type.equals("Lorry")) {
                line.append(",");  // numberOfDoors (empty for lorries)
                line.append(",");  // seatingCapacity (empty for lorries)
                line.append(load.isEmpty() ? "" : load).append(",");  // loadCapacity
            } else if (type.equals("Bus")) {
                line.append(",");  // numberOfDoors (empty for buses)
                line.append(seats.isEmpty() ? "" : seats).append(",");  // seatingCapacity
                line.append(",");  // loadCapacity (empty for buses)
            } else if (type.equals("Bike")) {
                line.append(",");  // numberOfDoors (empty for bikes)
                line.append(",");  // seatingCapacity (empty for bikes)
                line.append(",");  // loadCapacity (empty for bikes)
            } else {
                line.append(",,,");  // All empty for unknown types
            }
            
            line.append(photoPaths).append(",");
            line.append(availability).append(",");
            // Escape newlines and quotes in description for CSV
            String escapedDesc = description.replace("\n", "\\n").replace("\r", "");
            line.append(escapedDesc);
            bw.write(line.toString());
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error adding vehicle to CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteVehicleFromCSV(String regNumber) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(regNumber + ",")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error deleting vehicle from CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateVehicleAvailabilityInCSV(String regNumber, String newAvailability) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(regNumber + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 9) {
                        // Update availability (column 8, index 8)
                        parts[8] = newAvailability;
                        line = String.join(",", parts);
                    } else if (parts.length == 8) {
                        // Add availability if it doesn't exist
                        line = line + "," + newAvailability;
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error updating vehicle availability: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicleSeatingCapacityInCSV(String regNumber, String newSeats) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(regNumber + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 6) {
                        // Update seating capacity (column index 5)
                        parts[5] = newSeats;
                        line = String.join(",", parts);
                    } else {
                        // Pad missing columns up to seating capacity index
                        java.util.List<String> partsList = new ArrayList<>(java.util.Arrays.asList(parts));
                        while (partsList.size() < 6) partsList.add("");
                        partsList.set(5, newSeats);
                        line = String.join(",", partsList);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String ln : lines) {
                bw.write(ln);
                bw.newLine();
            }
            // Show success message
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Seating capacity updated successfully for " + regNumber,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error updating vehicle seating capacity: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicleLoadCapacityInCSV(String regNumber, String newLoad) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Data/vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(regNumber + ",")) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 7) {
                        // Update load capacity (column index 6)
                        parts[6] = newLoad;
                        line = String.join(",", parts);
                    } else {
                        // Pad missing columns up to load capacity index
                        java.util.List<String> partsList = new ArrayList<>(java.util.Arrays.asList(parts));
                        while (partsList.size() < 7) partsList.add("");
                        partsList.set(6, newLoad);
                        line = String.join(",", partsList);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error reading vehicles CSV: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Data/vehicles.csv"))) {
            for (String ln : lines) {
                bw.write(ln);
                bw.newLine();
            }
            // Show success message
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Load capacity updated successfully for " + regNumber,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error updating vehicle load capacity: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
