package org.aaabilling.billing.repository;

import org.aaabilling.billing.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByIsDelinquent(Boolean isDelinquent);
    List<Policy> findByLastPaymentStatus(String status);
}
