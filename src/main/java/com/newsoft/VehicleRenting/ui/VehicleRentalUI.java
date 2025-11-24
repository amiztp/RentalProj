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
        
        titlePanel.add(leftTitlePanel, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
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
        JMenuItem exitItem = new JMenuItem("Exit");
        
        refreshItem.addActionListener(e -> loadVehicles());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(refreshItem);
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
        
        photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16)); // 16px spacing (2 units)
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
        JButton searchBtn = createSecondaryButton("Search Vehicle");
        JButton exitBtn = createExitButton("Exit");
        
        refreshBtn.addActionListener(e -> loadVehicles());
        searchBtn.addActionListener(e -> searchVehicle());
        exitBtn.addActionListener(e -> System.exit(0));
        
        panel.add(refreshBtn);
        panel.add(searchBtn);
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
                    
                    for (Vehicle v : vehicles) {
                        String type = v.getClass().getSimpleName();
                        String model = getVehicleModel(v);
                        String availability = "Available"; // Default to Available
                        
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
        selectButton.addActionListener(e -> {
            showCustomerDetailsDialog(vehicle);
        });
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
        infoCard.add(buttonPanel, BorderLayout.SOUTH);
        
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
                File imgFile = new File("data/" + path);
                
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
                
                // Caption below (filename)
                JLabel pathLabel = new JLabel(new File(path).getName(), JLabel.CENTER);
                pathLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Small helper: 11pt Regular
                pathLabel.setForeground(new Color(107, 114, 128)); // Secondary Text #6B7280
                photoCard.add(pathLabel, BorderLayout.SOUTH);
                
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
                // Success message
                JOptionPane.showMessageDialog(dialog,
                    "Customer Details Submitted Successfully!\n\n" +
                    "Name: " + fullName + "\n" +
                    "NIC: " + nic + "\n" +
                    "Phone: " + phone + "\n" +
                    "License: " + license + "\n" +
                    "Email: " + email + "\n\n" +
                    "Vehicle: " + getVehicleModel(vehicle) + " (" + vehicle.getRegisterNumber() + ")",
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
                JOptionPane.showMessageDialog(loginDialog,
                    "Login Successful!\n\nCompany interface coming soon.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loginDialog.dispose();
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
}
