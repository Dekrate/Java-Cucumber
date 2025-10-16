package dojo.supermarket.model.loyalty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages loyalty strategies for all customers.
 * Supports multiple loyalty program types through LoyaltyStrategy interface.
 * Complies with Open/Closed Principle - open for extension, closed for modification.
 */
public class LoyaltyProgramManager {
    private Map<String, LoyaltyStrategy> customerStrategies;

    public LoyaltyProgramManager() {
        this.customerStrategies = new HashMap<>();
    }

    /**
     * Registers a customer with any loyalty strategy.
     * Allows extension with new loyalty types without modifying this class.
     */
    public void registerCustomerWithStrategy(String customerId, LoyaltyStrategy strategy) {
        customerStrategies.put(customerId, strategy);
    }

    /**
     * Registers a customer with default LoyaltyProgram.
     * Maintains backward compatibility.
     */
    public LoyaltyProgram registerCustomer(String customerId) {
        if (!customerStrategies.containsKey(customerId)) {
            LoyaltyProgram program = new LoyaltyProgram(customerId);
            customerStrategies.put(customerId, program);
            return program;
        }
        LoyaltyStrategy strategy = customerStrategies.get(customerId);
        return (strategy instanceof LoyaltyProgram) ? (LoyaltyProgram) strategy : null;
    }

    public Optional<LoyaltyStrategy> getCustomerStrategy(String customerId) {
        return Optional.ofNullable(customerStrategies.get(customerId));
    }

    /**
     * Gets customer program for backward compatibility.
     */
    public Optional<LoyaltyProgram> getCustomerProgram(String customerId) {
        LoyaltyStrategy strategy = customerStrategies.get(customerId);
        if (strategy instanceof LoyaltyProgram) {
            return Optional.of((LoyaltyProgram) strategy);
        }
        return Optional.empty();
    }

    /**
     * Awards points to a customer based on purchase amount.
     * Uses the strategy's point calculation method.
     */
    public void awardPointsForPurchase(String customerId, double purchaseAmount) {
        getCustomerProgram(customerId).ifPresent(program -> {
            int points = program.calculatePoints(purchaseAmount);
            program.addPoints(points);
        });
    }

    /**
     * Calculates the final price after applying loyalty discount.
     * Works with any LoyaltyStrategy implementation.
     */
    public double applyLoyaltyDiscount(String customerId, double originalPrice) {
        return getCustomerStrategy(customerId)
            .map(strategy -> originalPrice * (1 - strategy.calculateDiscount() / 100.0))
            .orElse(originalPrice);
    }

    public Map<String, LoyaltyProgram> getAllCustomerPrograms() {
        Map<String, LoyaltyProgram> programs = new HashMap<>();
        customerStrategies.forEach((id, strategy) -> {
            if (strategy instanceof LoyaltyProgram) {
                programs.put(id, (LoyaltyProgram) strategy);
            }
        });
        return programs;
    }

    public Map<String, LoyaltyStrategy> getAllCustomerStrategies() {
        return new HashMap<>(customerStrategies);
    }
}
