package org.aaabilling.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long policyId,
        BigDecimal amount,
        String status, // e.g., "COMPLETED", "FAILED"
        String message,
        LocalDateTime transactionDate,
        String referenceCode,
        String paymentMethod
) {}