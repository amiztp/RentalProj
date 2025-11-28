// ABSTRACTION: Payment is an abstract class exposing a common interface (process()) and shared fields. 
// Concrete subclasses implement process() and provide details.

package main.java.com.newsoft.VehicleRenting.payments;

import java.time.LocalDateTime;
import java.nio.file.*;
import java.io.*;

/**
 * Abstract base class for all payment types in the Vehicle Renting System.
 * Provides common payment properties and defines the contract for processing payments.
 */
public abstract class Payment {
    private static int nextIdNumber = 1;
    private static boolean initialized = false;
    protected String paymentId;
    protected String payerName;
    protected double amount;
    protected String vehicleReg;
    protected String nic;
    protected String phone;
    protected String address;
    protected String licenseNumber;
    protected String email;
    protected String paymentMethod;
    protected LocalDateTime timestamp;

    /**
     * Constructor for Payment.
     * 
     * @param payerName the name of the person making the payment
     * @param amount the payment amount
     * @param vehicleReg the vehicle registration number
     * @param nic the NIC number
     * @param phone the phone number
     * @param address the address
     * @param licenseNumber the license number
     * @param email the email address
     * @param paymentMethod the method of payment
     * @throws IllegalArgumentException if payerName is null/empty or amount is not positive
     */
    public Payment(String payerName, double amount, String vehicleReg, String nic, String phone, String address, String licenseNumber, String email, String paymentMethod) {
        if (payerName == null || payerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Payer name cannot be null or empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        // Initialize next ID number from file if not already done
        if (!initialized) {
            initializeIdCounter();
            initialized = true;
        }
        
        this.paymentId = generatePaymentId();
        this.payerName = payerName;
        this.amount = amount;
        this.vehicleReg = vehicleReg != null ? vehicleReg : "";
        this.nic = nic != null ? nic : "";
        this.phone = phone != null ? phone : "";
        this.address = address != null ? address : "";
        this.licenseNumber = licenseNumber != null ? licenseNumber : "";
        this.email = email != null ? email : "";
        this.paymentMethod = paymentMethod != null ? paymentMethod : "";
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Initializes the ID counter by reading the last payment ID from the CSV file.
     */
    private static synchronized void initializeIdCounter() {
        Path csvPath = Paths.get("Data", "payments.csv");
        
        if (Files.exists(csvPath)) {
            try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
                String line;
                String lastId = null;
                
                // Read all lines to find the last payment ID
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("id,")) {
                        // Extract the ID (first field)
                        int commaIndex = line.indexOf(',');
                        if (commaIndex > 0) {
                            lastId = line.substring(0, commaIndex);
                        }
                    }
                }
                
                // Parse the last ID to get the next number
                if (lastId != null && lastId.startsWith("PAY-")) {
                    try {
                        String numberPart = lastId.substring(4);
                        int lastNumber = Integer.parseInt(numberPart);
                        nextIdNumber = lastNumber + 1;
                    } catch (NumberFormatException e) {
                        // Keep default if parsing fails
                    }
                }
                
            } catch (IOException e) {
                // Keep default if file read fails
            }
        }
    }
    
    /**
     * Generates a readable payment ID in format PAY-00001.
     * 
     * @return formatted payment ID
     */
    private static synchronized String generatePaymentId() {
        return String.format("PAY-%05d", nextIdNumber++);
    }

    /**
     * Gets the unique payment identifier.
     * 
     * @return the payment ID
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Gets the name of the payer.
     * 
     * @return the payer name
     */
    public String getPayerName() {
        return payerName;
    }

    /**
     * Gets the payment amount.
     * 
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the vehicle registration number.
     * 
     * @return the vehicle registration
     */
    public String getVehicleReg() {
        return vehicleReg;
    }

    /**
     * Gets the NIC number.
     * 
     * @return the NIC number
     */
    public String getNic() {
        return nic;
    }

    /**
     * Gets the payment method.
     * 
     * @return the payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Gets the phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Gets the address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the license number.
     * 
     * @return the license number
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * Gets the email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the timestamp of when the payment was created.
     * 
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Abstract method to process the payment.
     * Subclasses must implement their specific payment processing logic.
     * 
     * @return true if payment processing was successful, false otherwise
     */
    public abstract boolean process();

    /**
     * Converts the payment to a CSV row format.
     * Format: id,payerName,nic,phone,address,licenseNumber,email,amount,paymentMethod,timestamp
     * 
     * @return CSV-safe string representation
     */
    public String toCsvRow() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s",
                paymentId,
                escapeCsv(payerName),
                escapeCsv(nic),
                escapeCsv(phone),
                escapeCsv(address),
                escapeCsv(licenseNumber),
                escapeCsv(email),
                amount,
                escapeCsv(paymentMethod),
                timestamp.toString());
    }

    /**
     * Gets payment-specific details. Subclasses can override to provide additional information.
     * 
     * @return payment details or empty string
     */
    protected String getPaymentDetails() {
        return "";
    }

    /**
     * Escapes special characters for CSV format.
     * 
     * @param value the value to escape
     * @return escaped value
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @Override
    public String toString() {
        return String.format("Payment[id=%s, payer=%s, amount=%.2f, method=%s, timestamp=%s]",
                paymentId, payerName, amount, paymentMethod, timestamp);
    }
}
