package org.aaabilling.billing.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(name = "Policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyHolder;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isDelinquent;

    // Will be set to FAILED or SUCCESS based on posted payments. Initial value is NONE
    private String lastPaymentStatus;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List <PolicySchedule> schedules;

    public Policy()
    {}

    public Policy(String policyHolder, BigDecimal premiumAmount, LocalDate startDate, LocalDate endDate) {
        this.policyHolder = policyHolder;
        this.premiumAmount = premiumAmount.setScale(2, RoundingMode.HALF_UP);
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDelinquent = false;
        this.lastPaymentStatus = "NONE"; // Will be st to FAILED or SUCCESS based on posted payments

    }

    public String getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(String policyHolder) {
        this.policyHolder = policyHolder;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount.setScale(2,RoundingMode.HALF_UP);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getDelinquent() {
        return isDelinquent;
    }

    public void setDelinquent(Boolean delinquent) {
        isDelinquent = delinquent;
    }

    public List<PolicySchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<PolicySchedule> schedules) {
        this.schedules = schedules;
    }

    public void addSchedule(PolicySchedule schedule) {
        schedules.add(schedule);
        schedule.setPolicy(this); //Associate to this policy
    }

    public String getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    public void setLastPaymentStatus(String lastPaymentStatus) {
        this.lastPaymentStatus = lastPaymentStatus;
    }
}
