package org.aaabilling.billing.repository;

import org.aaabilling.billing.models.Payment;
import org.aaabilling.billing.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PaymentRepository extends JpaRepository<Payment, Long> {
    public Payment findFirstByPolicyOrderByPaymentDateTimeDesc(Policy policy);
}
