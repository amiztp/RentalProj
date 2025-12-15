// Polymorphism: CardPayment.process() is a concrete implementation of Payment.process() for card payments.
// WARNING: Do not store real PANs — simulation only.

package main.java.com.newsoft.VehicleRenting.payments;

public class CardPayment extends Payment {
    private String maskedCardNumber;
    private String cardHolder;
    private String expiry;

    public CardPayment(String payerName, double amount, String vehicleReg, String nic, String phone, String address, String licenseNumber, String email, String paymentMethod, String cardHolder, String cardNumber, String expiry) {
        super(payerName, amount, vehicleReg, nic, phone, address, licenseNumber, email, paymentMethod);
        
        // Validate inputs
        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            throw new IllegalArgumentException("Card holder name cannot be null or empty");
        }
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Card number cannot be null or empty");
        }
        if (expiry == null || expiry.trim().isEmpty()) {
            throw new IllegalArgumentException("Expiry date cannot be null or empty");
        }
        
        this.cardHolder = cardHolder.trim();
        this.maskedCardNumber = maskCardNumber(cardNumber);
        this.expiry = expiry.trim();
    }

    /**
     * Masks the card number, showing only the last 4 digits.
     * All other digits are replaced with '*'.
     * 
     * @param cardNumber the full card number
     * @return the masked card number
     */
    private String maskCardNumber(String cardNumber) {
        // Remove all non-digit characters
        String digitsOnly = cardNumber.replaceAll("\\D", "");
        
        if (digitsOnly.length() < 4) {
            // If less than 4 digits, mask all
            return "*".repeat(digitsOnly.length());
        }
        
        // Keep last 4 digits, mask the rest
        String lastFour = digitsOnly.substring(digitsOnly.length() - 4);
        String masked = "*".repeat(digitsOnly.length() - 4) + lastFour;
        
        return masked;
    }

    /**
     * Processes the card payment.
     * Validates card details and simulates payment processing.
     * 
     * @return true if payment processing was successful, false otherwise
     */
    @Override
    public boolean process() {
        int totalDigits = maskedCardNumber.replaceAll("\\D", "").length();
        
        // Validate card number length (at least 12 digits) and expiry
        if (totalDigits < 12 || expiry.isEmpty()) {
            return false;
        }
        
        // Simulate payment processing delay
        try {
            Thread.sleep(80);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        return true;
    }

    /**
     * Gets the payment details for CSV export.
     * 
     * @return masked card number and card holder separated by semicolon
     */
    public String getDetailsForCsv() {
        return maskedCardNumber + ";" + cardHolder;
    }

    /**
     * Gets payment-specific details for the parent class toCsvRow method.
     * 
     * @return payment details
     */
    @Override
    protected String getPaymentDetails() {
        return getDetailsForCsv();
    }

    /**
     * Gets the masked card number.
     * 
     * @return the masked card number
     */
    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    /**
     * Gets the card holder name.
     * 
     * @return the card holder name
     */
    public String getCardHolder() {
        return cardHolder;
    }

    /**
     * Gets the card expiry date.
     * 
     * @return the expiry date
     */
    public String getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return String.format("CardPayment[id=%s, payer=%s, amount=%.2f, cardHolder=%s, card=%s, expiry=%s, timestamp=%s]",
                getPaymentId(), getPayerName(), getAmount(), cardHolder, maskedCardNumber, expiry, getTimestamp());
    }
}
