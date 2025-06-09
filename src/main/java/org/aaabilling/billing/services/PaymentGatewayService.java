package org.aaabilling.billing.services;

import org.aaabilling.billing.models.Policy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentGatewayService {
    // --- Simulation of Payment Gateway ---
    public boolean simulatePaymentGatewayCall(Policy policy, BigDecimal amount) {
        // Simulate to return true for even numbered policy and false of odd numbered
        return policy.getId() % 2 == 0;

    }
}
