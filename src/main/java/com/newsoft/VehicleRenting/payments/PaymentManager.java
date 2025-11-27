package main.java.com.newsoft.VehicleRenting.payments;

import java.nio.file.*;
import java.io.*;
import java.util.*;

/**
 * Manages payment processing and persistence to CSV storage.
 * Handles file I/O operations for recording and retrieving payment history.
 */
public class PaymentManager {
    private final Path csvPath = Paths.get("Data", "payments.csv");

    /**
     * Accepts and processes a payment, recording it to persistent storage if successful.
     * File I/O: Creates the Data directory if needed, appends payment records to CSV.
     * Exception handling: Catches IOExceptions during file operations and returns false on failure.
     * 
     * @param p the Payment to process
     * @return true if payment was processed and recorded successfully, false otherwise
     */
    public boolean acceptPayment(Payment p) {
        // Process the payment using polymorphic process() method
        boolean ok = p.process();
        
        if (ok) {
            try {
                // Ensure parent directory exists
                if (csvPath.getParent() != null) {
                    Files.createDirectories(csvPath.getParent());
                }
                
                // Check if file is new or empty to determine if header is needed
                boolean isNew = Files.notExists(csvPath) || Files.size(csvPath) == 0;
                
                // Open file in append mode with auto-flush
                try (BufferedWriter writer = Files.newBufferedWriter(csvPath, 
                        StandardOpenOption.CREATE, 
                        StandardOpenOption.APPEND)) {
                    
                    // Write header if this is a new file
                    if (isNew) {
                        writer.write("id,payerName,amount,vehicleReg,nic,paymentMethod,timestamp");
                        writer.newLine();
                    }
                    
                    // Write payment directly to CSV
                    writer.write(p.toCsvRow());
                    writer.newLine();
                    writer.flush();
                    
                    return true;
                }
                
            } catch (IOException e) {
                // Handle file I/O exceptions
                System.err.println("Failed to write payment to file: " + e.getMessage());
                return false;
            }
        }
        
        return false;
    }

    /**
     * Loads all payment records from CSV storage.
     * File I/O: Reads CSV file line by line, skipping header and malformed entries.
     * Exception handling: Returns empty list if file doesn't exist or read fails.
     * 
     * @return list of PaymentRecord objects, or empty list if file doesn't exist or read fails
     */
    public List<PaymentRecord> loadAllPayments() {
        List<PaymentRecord> payments = new ArrayList<>();
        
        // Return empty list if file doesn't exist
        if (Files.notExists(csvPath)) {
            return payments;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Parse CSV row into PaymentRecord
                try {
                    PaymentRecord record = parseCsvRow(line);
                    payments.add(record);
                } catch (Exception e) {
                    // Skip malformed rows
                    System.err.println("Skipping malformed payment record: " + line);
                }
            }
            
        } catch (IOException e) {
            // Handle file I/O exceptions
            System.err.println("Failed to read payments from file: " + e.getMessage());
            return new ArrayList<>();
        }
        
        return payments;
    }

    /**
     * Parses a CSV row into a PaymentRecord.
     * Handles CSV escaping (quoted fields with commas).
     * 
     * @param line the CSV line to parse
     * @return a PaymentRecord
     * @throws IllegalArgumentException if the row is malformed
     */
    private PaymentRecord parseCsvRow(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Handle escaped quotes
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString());
        
        // Validate field count
        if (fields.size() != 6) {
            throw new IllegalArgumentException("Expected 6 fields, found " + fields.size());
        }
        
        // Parse fields: id,payerName,amount,method,details,timestamp
        String id = fields.get(0);
        String payerName = fields.get(1);
        double amount = Double.parseDouble(fields.get(2));
        String method = fields.get(3);
        String details = fields.get(4);
        String timestamp = fields.get(5);
        
        return new PaymentRecord(id, payerName, amount, method, details, timestamp);
    }

    /**
     * Gets the path to the payments CSV file.
     * 
     * @return the CSV file path
     */
    public Path getCsvPath() {
        return csvPath;
    }
}
