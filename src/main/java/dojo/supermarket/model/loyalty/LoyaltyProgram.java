package dojo.supermarket.model.loyalty;

/**
 * Represents a customer loyalty program tier.
 * Different tiers provide different discount rates.
 *
 * This design is open for extension - new loyalty tiers can be added
 * without modifying existing code.
 */
public interface LoyaltyProgram {

    /**
     * Gets the name of this loyalty tier.
     */
    String getTierName();

    /**
     * Gets the discount percentage for this tier.
     */
    double getDiscountPercentage();

    /**
     * Gets the points multiplier for purchases in this tier.
     */
    double getPointsMultiplier();

    /**
     * Checks if this tier is applicable for the given purchase amount.
     */
    boolean isApplicable(double totalAmount);
}

