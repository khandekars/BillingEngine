package org.aaabilling.billing.services;

import org.aaabilling.billing.models.Policy;
import org.aaabilling.billing.models.PolicySchedule;
import org.aaabilling.billing.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    public PolicyService() {

    }

    public List<Policy> getDelinquentPolicies() {
        return policyRepository.findByIsDelinquent(true);
    }

    public void setDelinquent(Policy policy) {
        policy.setDelinquent(true);
        policyRepository.save(policy);
    }

    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    //Create a new policy with 12 month payment schedule
    public Policy savePolicy(Policy policy) {
        createDefaultSchedule(policy);
        return policyRepository.save(policy);
    }



    //Create a default schedule when creating new policy starting from next month for one year
    //Assumption: All policies have one 12 months of recurring payments.
    //You can construct different schedules like quarterly payments, annual payments, etc.
    private void createDefaultSchedule(Policy policy) {
        BigDecimal monthlyPremium = policy.getPremiumAmount().divide(BigDecimal.valueOf(12),RoundingMode.HALF_UP);

        List<PolicySchedule> policyScheduleList = new ArrayList<>();

        for(int i = 0 ; i < 12; i++) {

            policyScheduleList.add(new PolicySchedule(policy,
                    policy.getStartDate().plusMonths(i),
                    monthlyPremium , "DUE"));
        }

        policy.setSchedules(policyScheduleList);
    }
}



