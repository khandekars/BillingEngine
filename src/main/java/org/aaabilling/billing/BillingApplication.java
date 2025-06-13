package org.aaabilling.billing;

import org.aaabilling.billing.dto.PaymentRequest;
import org.aaabilling.billing.dto.PaymentResponse;
import org.aaabilling.billing.models.Policy;
import org.aaabilling.billing.services.PaymentService;
import org.aaabilling.billing.services.PolicyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


@SpringBootApplication
public class BillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(PolicyService policyService, PaymentService paymentService) {
		return (args) -> {
			System.out.println("Testing Started");
			System.out.println("--- Populating H2 In-Memory Database ---");
			LocalDate startDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
			LocalDate endDate = startDate.plusYears(1).minusDays(1);
			// Save a few policies using the service layer
			Policy p1 = policyService.savePolicy(new Policy("Customer 1", new BigDecimal("10000"),
					startDate, endDate));

			Policy p2 = policyService.savePolicy(new Policy( "Customer 2", new BigDecimal("20000"),
					startDate, endDate));


			//Delinquent Policy
			Policy p3 =  policyService.savePolicy(new Policy( "Customer 3", new BigDecimal("30000"),
					startDate, endDate));

			policyService.setDelinquent(p3);


			// Simulate a successful payment for policy
			System.out.println("Testing SUCCESSFUL payment for Policy ID: " + p2.getId());
			PaymentRequest successfulPaymentRequest = new PaymentRequest();
			successfulPaymentRequest.setAmount(new BigDecimal("1000"));
			successfulPaymentRequest.setPaymentMethodType("Credit Card");

			PaymentResponse successfulPaymentResponse = paymentService.processPayment(p2.getId(), successfulPaymentRequest);
			System.out.println("Payment Response: " + successfulPaymentResponse + "\n\n");

			// Simulate a failed payment for policy
			System.out.println("Testing a Failed payment for Policy ID: " + p1.getId());
			PaymentRequest failedPaymentRequest = new PaymentRequest();
			failedPaymentRequest.setAmount(new BigDecimal("1000"));
			failedPaymentRequest.setPaymentMethodType("Failed");

			PaymentResponse failedlPaymentResponse = paymentService.processPayment(p1.getId(), failedPaymentRequest);
			System.out.println("Payment Response: " + failedlPaymentResponse + "\n\n");

			failedlPaymentResponse = paymentService.processPayment(p1.getId(), failedPaymentRequest);
			System.out.println("Payment Response: " + failedlPaymentResponse + "\n\n");

			// Should pickup only policyId 1 as it failed. Policy 3 is delinquent so should not pick it
			System.out.println("Test Retry failed payments\n\n");
			paymentService.retryFailedPayments();

			System.out.println("Retry testing completed for failed payments");

			//Test again. No policy should be picked up as retry service should always succeed
			System.out.println("Again Test Retry failed payments\n\n");
			paymentService.retryFailedPayments();

			System.out.println("Retry testing completed. Ready");
		};
	}
}
