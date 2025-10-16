package dojo.supermarket.model.loyalty;

/**
 * Gold loyalty tier - premium benefits for VIP customers.
 */
public class GoldLoyaltyTier implements LoyaltyProgram {

    @Override
    public String getTierName() {
        return "Gold";
    }

    @Override
    public double getDiscountPercentage() {
        return 10.0;
    }

    @Override
    public double getPointsMultiplier() {
        return 2.0;
    }

    @Override
    public boolean isApplicable(double totalAmount) {
        return totalAmount >= 50.0;
    }
}

