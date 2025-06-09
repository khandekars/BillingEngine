package org.aaabilling.billing.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PolicySchedule {

    @Id // Specifies the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    @JsonIgnore //Prevent recursion when sending HTTP response via PolicyController
    private Policy policy; // Associated Policy object


    private LocalDate paymentDueDate;
    private BigDecimal scheduledAmount;
    private String status; //Paid, Processing, Due or Delinquent


    // --- Constructors ---
    public PolicySchedule() {
    }

    public PolicySchedule(Policy policy,  LocalDate paymentDueDate, BigDecimal scheduledAmount, String status) {
        this.policy = policy;
        this.paymentDueDate = paymentDueDate;
        this.scheduledAmount = scheduledAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public BigDecimal getScheduledAmount() {
        return scheduledAmount;
    }

    public void setScheduledAmount(BigDecimal scheduledAmount) {
        this.scheduledAmount = scheduledAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}