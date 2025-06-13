package org.aaabilling.billing.services;


import org.aaabilling.billing.models.Payment;
import org.aaabilling.billing.models.Policy;
import org.aaabilling.billing.models.PolicySchedule;
import org.aaabilling.billing.repository.PaymentRepository;
import org.aaabilling.billing.repository.PolicyRepository;
import org.aaabilling.billing.repository.PolicyScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.aaabilling.billing.dto.PaymentRequest;
import org.aaabilling.billing.dto.PaymentResponse;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    // Dummy service to simulate payment
    @Autowired
    private  PaymentGatewayService paymentGatewayService;


    // Calls to these repositories can be delegated to respective services to keep code modular and address
    // separation of concerns
    // Used these directly here to improve readability

    @Autowired
    private  PolicyScheduleRepository policyScheduleRepository;

    @Autowired
    private  PolicyRepository policyRepository;

    public PaymentService() {

    }

    @Transactional
    public PaymentResponse processPayment(Long policyId, PaymentRequest request) {
        Optional<Policy> policyOptional = policyRepository.findById(policyId);
        if (policyOptional.isEmpty()) {
            return new PaymentResponse(null, policyId, request.getAmount(),
                    "FAILED", "Policy not found.", null, null, request.getPaymentMethodType());
        }
        Policy policy = policyOptional.get();
        Payment savedPayment = applyPayment(policy, request.getAmount(), request.getPaymentMethodType());

        String message = savedPayment.getStatus().equals("COMPLETED")? "Payment processed successfully." : "Payment failed at gateway";
        String transactionReference = savedPayment.getTransactionReference();

        return new PaymentResponse(
                savedPayment.getId(),
                policyId,
                savedPayment.getAmount(),
                savedPayment.getStatus(),
                message,
                savedPayment.getPaymentDateTime(),
                transactionReference,
                request.getPaymentMethodType()
        );

    }

    // Retry payments for all policies which have FAILED lastPaymentStatus
    // In real application this would be run as a batch job based on business rules which take into consideration
    // why the payment had failed

    @Transactional
    public boolean retryFailedPayments() {
       List<Policy> policyList = policyRepository.findByLastPaymentStatus("FAILED");

       String result =  policyList.stream().map(policy -> {
            BigDecimal amount = policyScheduleRepository.findFirstByPolicyAndStatusOrderByPaymentDueDateAsc(policy, "DUE").getScheduledAmount();
            Payment payment = applyPayment(policy,amount, "CC");
            return payment.getStatus();
        }).reduce("COMPLETED",(String res1, String res2) ->
       {  return res1.equals("FAILED") || res2.equals("FAILED") ? "FAILED" : "COMPLETED";
       });

       return result.equals("COMPLETED");
    }

    private Payment applyPayment(Policy policy, BigDecimal amount, String paymentMethodType) {
        String transactionReference = UUID.randomUUID().toString(); // Simulate gateway transaction ID
        String paymentStatus;
        String message;

        System.out.println("Retrying payment for policyId: " + policy.getId() + " payment type : "  + paymentMethodType);
        // Simulate Payment Gateway Call. This simply returns boolean. In real application this would return
        // transaction reference ID along with status. This referenceId is to correlate call to
        // the external Payment Gateway service

        boolean gatewaySuccess = paymentGatewayService.simulatePaymentGatewayCall(paymentMethodType);

        // Record the payment attempt regardless of success or failure


        paymentStatus =  gatewaySuccess ? "COMPLETED" : "FAILED";
        System.out.println("Status: " + paymentStatus);
        // Calculate retryCount for Payment record to be saved
        // if past payment is failed, return 0 if current payment succeeded else increment retry count of failed
        // if past payment is successful or none existed, return 0 if current payment succeeded else 1 for current failed payment
        Optional <Payment> payment = Optional.ofNullable(paymentRepository.findFirstByPolicyOrderByPaymentDateTimeDesc(policy));

        int retryCount = payment.filter(p -> p.getStatus().equals("FAILED"))
                .map(p -> gatewaySuccess? 0 : p.getRetryNumber() + 1)
                .orElseGet( () -> gatewaySuccess? 0 : 1 );

        // Save the  payment record
        Payment p = new Payment(
                policy,
                LocalDateTime.now(),
                amount,
                paymentStatus,
                retryCount,
                transactionReference,
                paymentMethodType
        );

        Payment savedPayment = paymentRepository.save(p);

        //Update the schedule
        //NOTE: There can be conditional logic to keep the status as DUE if the payment amount is less and the scheduled amount due
        //There is no handling for such logic in this implementation. code simply updates the first DUE record in the schedule

        //
        PolicySchedule policySchedule = policyScheduleRepository.findFirstByPolicyAndStatusOrderByPaymentDueDateAsc(policy,"FAILED");

        // If no failed policy status, apply payment towards next due date
        if(policySchedule == null) {
            policySchedule = policyScheduleRepository.findFirstByPolicyAndStatusOrderByPaymentDueDateAsc(policy, "DUE");
        }

        policySchedule.setStatus(paymentStatus);
        policyScheduleRepository.save(policySchedule);

        //Update policy status
        policy.setLastPaymentStatus(paymentStatus);

        return savedPayment;
    }



}