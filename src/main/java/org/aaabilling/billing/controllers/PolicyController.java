package org.aaabilling.billing.controllers;

import org.aaabilling.billing.dto.PaymentRequest;
import org.aaabilling.billing.dto.PaymentResponse;
import org.aaabilling.billing.models.Policy;
import org.aaabilling.billing.models.PolicySchedule;
import org.aaabilling.billing.services.PaymentService;
import org.aaabilling.billing.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {
    @Autowired
    private  PolicyService policyService;

    @Autowired
    private  PaymentService paymentService;

    public PolicyController(){
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        Optional<Policy> policy = policyService.getPolicyById(id);

        return policy.map(ResponseEntity::ok) // If policy is present, return 200 OK with body
                .orElseGet(() -> ResponseEntity.notFound().build()); // Else, return 404 Not Found
    }


    @GetMapping("/delinquent")
    public List<Policy> getDelinquentPolicies() {
        return policyService.getDelinquentPolicies();
    }


    @PostMapping("/policies/{policyId}/payment")
    public ResponseEntity<PaymentResponse> postPaymentForPolicy(
            @PathVariable Long policyId,
            @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(policyId, request);

        if ("FAILED".equals(response.status())) {
            //
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if ("Policy not found.".equals(response.message())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            return ResponseEntity.ok(response); // 200 OK for successful payment
        }
    }

    @PostMapping("/")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        Policy savedPolicy = policyService.savePolicy(policy);
        return new ResponseEntity<>(savedPolicy, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/schedule")
    public List<PolicySchedule> getPolicySchedule(@PathVariable Long id) {
        return policyService.getPolicyById(id).map(Policy::getSchedules).orElse(new ArrayList<>());
    }
}
