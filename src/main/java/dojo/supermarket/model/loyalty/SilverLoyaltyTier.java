package dojo.supermarket.model.loyalty;

/**
 * Silver loyalty tier - moderate benefits for regular customers.
 */
public class SilverLoyaltyTier implements LoyaltyProgram {

    @Override
    public String getTierName() {
        return "Silver";
    }

    @Override
    public double getDiscountPercentage() {
        return 5.0;
    }

    @Override
    public double getPointsMultiplier() {
        return 1.5;
    }

    @Override
    public boolean isApplicable(double totalAmount) {
        return totalAmount >= 20.0;
    }
}

