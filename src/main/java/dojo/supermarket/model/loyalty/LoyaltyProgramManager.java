

package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages customer loyalty programs and applies loyalty discounts.
 * Follows Open/Closed Principle - new loyalty tiers can be added without code changes.
 */
public class LoyaltyProgramManager {

    private final List<LoyaltyProgram> programs = new ArrayList<>();

    public LoyaltyProgramManager() {
        // Register default loyalty tiers
        programs.add(new GoldLoyaltyTier());
        programs.add(new SilverLoyaltyTier());
        programs.add(new BasicLoyaltyTier());
    }

    public void addLoyaltyProgram(LoyaltyProgram program) {
        programs.add(program);
    }

    /**
     * Determines the applicable loyalty tier based on purchase amount.
     * Returns the highest tier that the customer qualifies for.
     */
    public LoyaltyProgram getApplicableTier(double totalAmount) {
        for (LoyaltyProgram program : programs) {
            if (program.isApplicable(totalAmount)) {
                return program;
            }
        }
        return new BasicLoyaltyTier();
    }

    /**
     * Calculates loyalty discount based on the customer's tier.
     */
    public Discount calculateLoyaltyDiscount(double subtotal, Product representativeProduct) {
        LoyaltyProgram tier = getApplicableTier(subtotal);

        if (tier.getDiscountPercentage() > 0) {
            double discountAmount = subtotal * tier.getDiscountPercentage() / 100.0;
            String description = tier.getTierName() + " Member - " + tier.getDiscountPercentage() + "% off";
            return new Discount(representativeProduct, description, -discountAmount);
        }

        return null;
    }
}