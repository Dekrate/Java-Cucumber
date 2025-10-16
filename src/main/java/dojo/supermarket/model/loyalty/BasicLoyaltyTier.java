package dojo.supermarket.model.loyalty;

/**
 * Basic loyalty tier - minimal benefits for new customers.
 */
public class BasicLoyaltyTier implements LoyaltyProgram {

    @Override
    public String getTierName() {
        return "Basic";
    }

    @Override
    public double getDiscountPercentage() {
        return 0.0;
    }

    @Override
    public double getPointsMultiplier() {
        return 1.0;
    }

    @Override
    public boolean isApplicable(double totalAmount) {
        return true;
    }
}

