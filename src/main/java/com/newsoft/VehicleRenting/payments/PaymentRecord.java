package main.java.com.newsoft.VehicleRenting.payments;

/**
 * Immutable record of a payment transaction.
 * Used for storing and exporting payment history.
 */
public class PaymentRecord {
    private final String id;
    private final String payerName;
    private final double amount;
    private final String method;
    private final String details;
    private final String timestamp;

    /**
     * Constructor for PaymentRecord.
     * 
     * @param id the payment identifier
     * @param payerName the name of the payer
     * @param amount the payment amount
     * @param method the payment method
     * @param details additional payment details
     * @param timestamp the timestamp of the payment
     */
    public PaymentRecord(String id, String payerName, double amount, String method, String details, String timestamp) {
        this.id = id;
        this.payerName = payerName;
        this.amount = amount;
        this.method = method;
        this.details = details;
        this.timestamp = timestamp;
    }

    /**
     * Gets the payment ID.
     * 
     * @return the payment ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the payer name.
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
     * Gets the payment method.
     * 
     * @return the payment method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Gets the payment details.
     * 
     * @return the payment details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Gets the payment timestamp.
     * 
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Creates a PaymentRecord from a Payment object.
     * Uses polymorphism to extract type-specific details.
     * 
     * @param p the Payment object
     * @return a new PaymentRecord
     */
    public static PaymentRecord fromPayment(Payment p) {
        String details;
        
        // Use instanceof to extract payment-specific details
        if (p instanceof CardPayment) {
            details = ((CardPayment) p).getDetailsForCsv();
        } else if (p instanceof CashPayment) {
            details = ((CashPayment) p).getDetailsForCsv();
        } else {
            // Default for other payment types
            details = "";
        }
        
        return new PaymentRecord(
            p.getPaymentId(),
            p.getPayerName(),
            p.getAmount(),
            p.getPaymentMethod(),
            details,
            p.getTimestamp().toString()
        );
    }

    /**
     * Converts the payment record to a CSV row format.
     * Format: id,payerName,amount,method,details,timestamp
     * 
     * @return CSV-safe string representation
     */
    public String toCsvRow() {
        return String.format("%s,%s,%.2f,%s,%s,%s",
                escapeCsv(id),
                escapeCsv(payerName),
                amount,
                escapeCsv(method),
                escapeCsv(details),
                escapeCsv(timestamp));
    }

    /**
     * Escapes special characters for CSV format.
     * Handles commas, quotes, and newlines.
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
        return String.format("PaymentRecord[id=%s, payer=%s, amount=%.2f, method=%s, details=%s, timestamp=%s]",
                id, payerName, amount, method, details, timestamp);
    }
}
