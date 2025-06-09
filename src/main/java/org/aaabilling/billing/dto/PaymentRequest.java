package org.aaabilling.billing.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private BigDecimal amount;
    private String paymentMethodType;

    // Can have other details based on payment method provided.
    // Not using them to keep focus on key attributes
    // The payment would be applied to the next due payment on the policy
    // However, there can be business rules for past due payments or overpayments
    // which can dictate which PaymentSchedule this payment applies to.
    // We will only implement the default - apply to next due.

    // String bankAccountNo;
    // String bankRoutingNumber;
    // String creditCardNo;


    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethodType() { return paymentMethodType; }
    public void setPaymentMethodType(String paymentMethodType) { this.paymentMethodType = paymentMethodType; }
}