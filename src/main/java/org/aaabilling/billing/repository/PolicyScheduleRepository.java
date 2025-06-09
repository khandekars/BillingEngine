package org.aaabilling.billing.repository;

import org.aaabilling.billing.models.Policy;
import org.aaabilling.billing.models.PolicySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyScheduleRepository extends JpaRepository<PolicySchedule, Long> {
    public PolicySchedule findFirstByPolicyAndStatusOrderByPaymentDueDateAsc(Policy policy, String status);
}
