// Polymorphism: CashPayment.process() implements Payment.process() differently than CardPayment.

package main.java.com.newsoft.VehicleRenting.payments;

/**
 * Concrete payment implementation for cash-based payments.
 * Handles immediate cash payment processing without additional validation.
 */
public class CashPayment extends Payment {

    /**
     * Constructor for CashPayment.
     * 
     * @param payerName the name of the person making the payment
     * @param amount the payment amount
     * @param vehicleReg the vehicle registration number
     * @param nic the NIC number
     * @param phone the phone number
     * @param address the address
     * @param licenseNumber the license number
     * @param email the email address
     * @param paymentMethod the payment method
     * @throws IllegalArgumentException if payerName is invalid or amount is not positive
     */
    public CashPayment(String payerName, double amount, String vehicleReg, String nic, String phone, String address, String licenseNumber, String email, String paymentMethod) {
        super(payerName, amount, vehicleReg, nic, phone, address, licenseNumber, email, paymentMethod);
    }

    /**
     * Processes the cash payment.
     * Cash payments are processed immediately without additional validation.
     * 
     * @return true if amount is greater than zero, false otherwise
     */
    @Override
    public boolean process() {
        return amount > 0;
    }

    /**
     * Gets the payment details for CSV export.
     * Cash payments have no additional details to record.
     * 
     * @return "N/A" indicating no additional details
     */
    public String getDetailsForCsv() {
        return "N/A";
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

    @Override
    public String toString() {
        return String.format("CashPayment[id=%s, payer=%s, amount=%.2f, timestamp=%s]",
                getPaymentId(), getPayerName(), getAmount(), getTimestamp());
    }
}
