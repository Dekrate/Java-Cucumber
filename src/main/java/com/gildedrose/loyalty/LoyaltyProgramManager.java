package com.gildedrose.loyalty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages loyalty programs for all customers.
 */
public class LoyaltyProgramManager {
    private Map<String, LoyaltyProgram> customerPrograms;

    public LoyaltyProgramManager() {
        this.customerPrograms = new HashMap<>();
    }

    public LoyaltyProgram registerCustomer(String customerId) {
        if (!customerPrograms.containsKey(customerId)) {
            LoyaltyProgram program = new LoyaltyProgram(customerId);
            customerPrograms.put(customerId, program);
            return program;
        }
        return customerPrograms.get(customerId);
    }

    public Optional<LoyaltyProgram> getCustomerProgram(String customerId) {
        return Optional.ofNullable(customerPrograms.get(customerId));
    }

    /**
     * Awards points to a customer based on purchase amount.
     * 1 point per unit of currency spent.
     */
    public void awardPointsForPurchase(String customerId, double purchaseAmount) {
        getCustomerProgram(customerId).ifPresent(program -> {
            int points = (int) Math.floor(purchaseAmount);
            program.addPoints(points);
        });
    }

    /**
     * Calculates the final price after applying loyalty discount.
     */
    public double applyLoyaltyDiscount(String customerId, double originalPrice) {
        return getCustomerProgram(customerId)
            .map(program -> originalPrice * (1 - program.getTierDiscount() / 100.0))
            .orElse(originalPrice);
    }

    public Map<String, LoyaltyProgram> getAllCustomerPrograms() {
        return new HashMap<>(customerPrograms);
    }
}
