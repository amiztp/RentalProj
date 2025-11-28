// ABSTRACTION: the UI interacts with Payment via the abstract Payment type only.

package main.java.com.newsoft.VehicleRenting.payments;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User interface for processing payments in the Vehicle Renting System.
 * Supports both card and cash payment methods with polymorphic payment processing.
 */
public class PaymentsUI {
    private JPanel mainPanel;
    
    // Form fields
    private JTextField txtFullName;
    private JTextField txtNIC;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtLicenseNumber;
    private JTextField txtEmail;
    private JTextField txtAmount;
    private JTextField txtPaymentMethod;
    
    // Action and status
    private JButton btnSubmit;
    private JTextArea txtStatus;
    
    /**
     * Constructor - initializes and builds the payment menu UI.
     */
    public PaymentsUI() {
        buildMenuUI();
    }
    
    /**
     * Builds the main menu UI with Pay and Check Payments buttons.
     */
    private void buildMenuUI() {
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // Logo
        gbc.gridy = 0;
        java.io.File logoFile = new java.io.File("data/photos/logo.png");
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
            Image logoImg = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            mainPanel.add(logoLabel, gbc);
        } else {
            // Fallback emoji logo
            JLabel logoLabel = new JLabel("💳");
            logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(logoLabel, gbc);
        }
        
        // Title
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 15, 5, 15);
        JLabel lblTitle = new JLabel("Payment Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(11, 111, 175));
        mainPanel.add(lblTitle, gbc);
        
        // Subtitle
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 15, 30, 15);
        JLabel lblSubtitle = new JLabel("Select an option below");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(107, 114, 128));
        mainPanel.add(lblSubtitle, gbc);
        
        // Pay button
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 15, 10, 15);
        JButton btnPay = new JButton("Pay");
        btnPay.setFont(new Font("Arial", Font.BOLD, 16));
        btnPay.setPreferredSize(new Dimension(250, 55));
        btnPay.setBackground(new Color(11, 111, 175));
        btnPay.setForeground(Color.WHITE);
        btnPay.setFocusPainted(false);
        btnPay.setBorderPainted(false);
        btnPay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPay.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnPay.setBackground(new Color(9, 74, 122));
            }
            public void mouseExited(MouseEvent e) {
                btnPay.setBackground(new Color(11, 111, 175));
            }
            public void mousePressed(MouseEvent e) {
                btnPay.setBackground(new Color(6, 53, 87));
            }
            public void mouseReleased(MouseEvent e) {
                btnPay.setBackground(new Color(9, 74, 122));
            }
        });
        btnPay.addActionListener(e -> showPaymentForm());
        mainPanel.add(btnPay, gbc);
        
        // Check Payments button
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 15, 10, 15);
        JButton btnCheckPayments = new JButton("Check Payments");
        btnCheckPayments.setFont(new Font("Arial", Font.BOLD, 16));
        btnCheckPayments.setPreferredSize(new Dimension(250, 55));
        btnCheckPayments.setBackground(new Color(11, 111, 175));
        btnCheckPayments.setForeground(Color.WHITE);
        btnCheckPayments.setFocusPainted(false);
        btnCheckPayments.setBorderPainted(false);
        btnCheckPayments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckPayments.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnCheckPayments.setBackground(new Color(9, 74, 122));
            }
            public void mouseExited(MouseEvent e) {
                btnCheckPayments.setBackground(new Color(11, 111, 175));
            }
            public void mousePressed(MouseEvent e) {
                btnCheckPayments.setBackground(new Color(6, 53, 87));
            }
            public void mouseReleased(MouseEvent e) {
                btnCheckPayments.setBackground(new Color(9, 74, 122));
            }
        });
        btnCheckPayments.addActionListener(e -> showCheckPayments());
        mainPanel.add(btnCheckPayments, gbc);
    }
    
    /**
     * Shows the payment processing form.
     */
    private void showPaymentForm() {
        initializeComponents();
        buildPaymentFormUI();
        attachListeners();
    }
    
    /**
     * Shows the check payments interface in a large window.
     */
    private void showCheckPayments() {
        // Close the parent payment dialog first
        Window parentWindow = SwingUtilities.getWindowAncestor(mainPanel);
        if (parentWindow != null) {
            parentWindow.dispose();
        }
        
        // Create a new JFrame window for payment history (instead of JDialog)
        JFrame historyFrame = new JFrame("Drive Smart - Payment History");
        historyFrame.setSize(1100, 650);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Remove maximized state - open as normal window
        
        // Main panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(244, 247, 250));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(11, 111, 175));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Payment History");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);
        
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
        searchField.setToolTipText("Enter Payment ID or NIC");
        
        searchPanel.add(searchField);
        titlePanel.add(searchPanel, BorderLayout.EAST);
        
        contentPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table to display payments
        String[] columnNames = {"Payment ID", "Full Name", "NIC", "Phone", "Address", "License Number", "Email", "Amount", "Payment Method", "Date"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Store all payments data for filtering
        java.util.List<Object[]> allPaymentsData = new java.util.ArrayList<>();
        
        // Load payments from CSV
        loadPaymentsFromCSV(tableModel, allPaymentsData);
        
        // Add search functionality with KeyListener
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                filterPaymentTable(tableModel, allPaymentsData, searchText);
            }
        });
        
        JTable paymentsTable = new JTable(tableModel);
        paymentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentsTable.setRowHeight(35);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentsTable.setShowGrid(true);
        paymentsTable.setGridColor(new Color(215, 222, 230));
        paymentsTable.setBackground(Color.WHITE);
        paymentsTable.setSelectionBackground(new Color(230, 240, 250));
        paymentsTable.setSelectionForeground(new Color(34, 40, 49));
        
        // Style table header
        paymentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        paymentsTable.getTableHeader().setBackground(new Color(30, 144, 255));
        paymentsTable.getTableHeader().setForeground(Color.BLUE);
        paymentsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        paymentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Adjust column widths
        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Payment ID
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Full Name
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(120); // NIC
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Address
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // License Number
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Email
        paymentsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Amount
        paymentsTable.getColumnModel().getColumn(8).setPreferredWidth(130); // Payment Method
        paymentsTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Date
        
        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(215, 222, 230), 1));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setBackground(new Color(244, 247, 250));
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        btnRefresh.setBackground(new Color(11, 111, 175));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> {
            searchField.setText("");
            tableModel.setRowCount(0);
            allPaymentsData.clear();
            loadPaymentsFromCSV(tableModel, allPaymentsData);
        });
        
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.setBackground(new Color(107, 114, 128));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> historyFrame.dispose());
        
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        historyFrame.setContentPane(contentPanel);
        historyFrame.setAlwaysOnTop(true);
        historyFrame.setVisible(true);
        historyFrame.toFront();
        historyFrame.requestFocus();
        historyFrame.setAlwaysOnTop(false);
    }
    
    /**
     * Loads payments from CSV file into the table model and stores all data.
     */
    private void loadPaymentsFromCSV(javax.swing.table.DefaultTableModel tableModel, java.util.List<Object[]> allPaymentsData) {
        java.io.File csvFile = new java.io.File("Data/payments.csv");
        
        if (!csvFile.exists()) {
            tableModel.addRow(new Object[]{"No payments found", "", "", "", "", "", ""});
            return;
        }
        
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse CSV row
                String[] fields = parseCSVLine(line);
                
                if (fields.length >= 10) {
                    // Format: paymentId,payerName,nic,phone,address,licenseNumber,email,amount,paymentMethod,timestamp
                    Object[] rowData = new Object[]{
                        fields[0], // Payment ID
                        fields[1], // Full Name
                        fields[2], // NIC
                        fields[3], // Phone
                        fields[4], // Address
                        fields[5], // License Number
                        fields[6], // Email
                        "Rs." + fields[7], // Amount in Rupees
                        fields[8], // Payment Method
                        formatDateTime(fields[9]) // Date only
                    };
                    tableModel.addRow(rowData);
                    allPaymentsData.add(rowData);
                }
            }
            
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No payments recorded yet", "", "", "", "", "", "", "", "", ""});
            }
            
        } catch (java.io.IOException e) {
            tableModel.addRow(new Object[]{"Error reading payments: " + e.getMessage(), "", "", "", "", "", "", "", "", ""});
        }
    }
    
    /**
     * Filters payment table based on search text.
     * Searches in Payment ID and NIC columns.
     */
    private void filterPaymentTable(javax.swing.table.DefaultTableModel tableModel, 
                                     java.util.List<Object[]> allPaymentsData, 
                                     String searchText) {
        tableModel.setRowCount(0);
        
        if (searchText.isEmpty()) {
            // Show all payments
            for (Object[] rowData : allPaymentsData) {
                tableModel.addRow(rowData);
            }
        } else {
            // Filter payments
            boolean found = false;
            for (Object[] rowData : allPaymentsData) {
                String paymentId = rowData[0].toString().toLowerCase();
                String nic = rowData[2].toString().toLowerCase();
                
                if (paymentId.contains(searchText) || nic.contains(searchText)) {
                    tableModel.addRow(rowData);
                    found = true;
                }
            }
            
            if (!found) {
                tableModel.addRow(new Object[]{"No matching payments found", "", "", "", "", "", "", "", "", ""});
            }
        }
    }
    
    /**
     * Parses a CSV line handling quoted fields.
     */
    private String[] parseCSVLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString().trim());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Formats date time string to show only date.
     */
    private String formatDateTime(String timestamp) {
        try {
            // Extract only the date part (before 'T' or space)
            if (timestamp.contains("T")) {
                return timestamp.substring(0, timestamp.indexOf("T"));
            } else if (timestamp.contains(" ")) {
                return timestamp.substring(0, timestamp.indexOf(" "));
            }
            return timestamp;
        } catch (Exception e) {
            return timestamp;
        }
    }
    
    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        // Form fields
        txtFullName = new JTextField(20);
        txtNIC = new JTextField(20);
        txtPhone = new JTextField(20);
        txtAddress = new JTextField(20);
        txtLicenseNumber = new JTextField(20);
        txtEmail = new JTextField(20);
        txtAmount = new JTextField(20);
        txtPaymentMethod = new JTextField(20);
        
        // Submit button
        btnSubmit = new JButton("Process Payment");
        
        // Status area
        txtStatus = new JTextArea(5, 30);
        txtStatus.setEditable(false);
        txtStatus.setLineWrap(true);
        txtStatus.setWrapStyleWord(true);
        txtStatus.setBackground(new Color(240, 240, 240));
        txtStatus.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    /**
     * Builds the payment form UI layout using GridBagLayout.
     */
    private void buildPaymentFormUI() {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblTitle = new JLabel("Payment Processing");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(lblTitle, gbc);
        
        // Reset for next rows
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Full Name
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtFullName, gbc);
        row++;
        
        // NIC Number
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("NIC Number:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtNIC, gbc);
        row++;
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtPhone, gbc);
        row++;
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtAddress, gbc);
        row++;
        
        // License Number
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("License Number:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtLicenseNumber, gbc);
        row++;
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtEmail, gbc);
        row++;
        
        // Amount
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Amount:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtAmount, gbc);
        row++;
        
        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Payment Method:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(txtPaymentMethod, gbc);
        row++;
        
        // Submit button
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 10, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        
        btnSubmit.setPreferredSize(new Dimension(160, 35));
        mainPanel.add(btnSubmit, gbc);
        
        // Status area
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JScrollPane scrollPane = new JScrollPane(txtStatus);
        mainPanel.add(scrollPane, gbc);
    }
    
    /**
     * Attaches event listeners to UI components.
     */
    private void attachListeners() {
        // Submit button listener
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
    }
    
    /**
     * Processes the payment based on user input.
     * Demonstrates polymorphism by using Payment reference type.
     */
    private void processPayment() {
        try {
            // Validate common fields
            String fullName = txtFullName.getText().trim();
            if (fullName.isEmpty()) {
                txtStatus.setText("ERROR: Full Name is required.");
                return;
            }
            
            String nic = txtNIC.getText().trim();
            if (nic.isEmpty()) {
                txtStatus.setText("ERROR: NIC Number is required.");
                return;
            }
            
            String phone = txtPhone.getText().trim();
            if (phone.isEmpty()) {
                txtStatus.setText("ERROR: Phone is required.");
                return;
            }
            
            String address = txtAddress.getText().trim();
            if (address.isEmpty()) {
                txtStatus.setText("ERROR: Address is required.");
                return;
            }
            
            String licenseNumber = txtLicenseNumber.getText().trim();
            if (licenseNumber.isEmpty()) {
                txtStatus.setText("ERROR: License Number is required.");
                return;
            }
            
            String email = txtEmail.getText().trim();
            if (email.isEmpty()) {
                txtStatus.setText("ERROR: Email is required.");
                return;
            }
            
            String amountStr = txtAmount.getText().trim();
            if (amountStr.isEmpty()) {
                txtStatus.setText("ERROR: Amount is required.");
                return;
            }
            
            String paymentMethod = txtPaymentMethod.getText().trim();
            if (paymentMethod.isEmpty()) {
                txtStatus.setText("ERROR: Payment Method is required.");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    txtStatus.setText("ERROR: Amount must be greater than zero.");
                    return;
                }
            } catch (NumberFormatException ex) {
                txtStatus.setText("ERROR: Invalid amount format. Please enter a valid number.");
                return;
            }
            
            // Create CashPayment (using abstract Payment type)
            Payment p = new CashPayment(fullName, amount, "", nic, phone, address, licenseNumber, email, paymentMethod);
            
            // Process payment using polymorphic method
            PaymentManager pm = new PaymentManager();
            boolean saved = pm.acceptPayment(p);
            
            if (saved) {
                txtStatus.setText(String.format(
                    "SUCCESS!\n\n" +
                    "Payment ID: %s\n" +
                    "Full Name: %s\n" +
                    "NIC: %s\n" +
                    "Phone: %s\n" +
                    "Address: %s\n" +
                    "License Number: %s\n" +
                    "Email: %s\n" +
                    "Amount: $%.2f\n" +
                    "Payment Method: %s\n" +
                    "Timestamp: %s\n\n" +
                    "Payment processed and recorded successfully.",
                    p.getPaymentId(),
                    p.getPayerName(),
                    p.getNic(),
                    p.getPhone(),
                    p.getAddress(),
                    p.getLicenseNumber(),
                    p.getEmail(),
                    p.getAmount(),
                    paymentMethod,
                    p.getTimestamp()
                ));
                
                // Clear form after successful payment
                clearForm();
            } else {
                txtStatus.setText(String.format(
                    "PAYMENT FAILED!\n\n" +
                    "The payment could not be processed.\n" +
                    "Please verify your payment details and try again."
                ));
            }
            
        } catch (IllegalArgumentException ex) {
            txtStatus.setText("ERROR: " + ex.getMessage());
        } catch (Exception ex) {
            txtStatus.setText("ERROR: An unexpected error occurred: " + ex.getMessage());
        }
    }
    
    /**
     * Clears the form after successful payment.
     */
    private void clearForm() {
        txtFullName.setText("");
        txtNIC.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtLicenseNumber.setText("");
        txtEmail.setText("");
        txtAmount.setText("");
        txtPaymentMethod.setText("");
    }
    
    /**
     * Gets the main payment panel for integration into other UIs.
     * 
     * @return the payment panel
     */
    public JPanel getPaymentPanel() {
        return mainPanel;
    }
    
    /**
     * Main method for testing the PaymentsUI standalone.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Payment Processing System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                PaymentsUI paymentsUI = new PaymentsUI();
                frame.add(paymentsUI.getPaymentPanel());
                
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
