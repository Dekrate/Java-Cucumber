package com.gildedrose.loyalty;

/**
 * Represents a customer's loyalty program membership.
 */
public class LoyaltyProgram {
    private String customerId;
    private int points;
    private LoyaltyTier tier;

    public LoyaltyProgram(String customerId) {
        this.customerId = customerId;
        this.points = 0;
        this.tier = LoyaltyTier.BRONZE;
    }

    public void addPoints(int pointsToAdd) {
        points += pointsToAdd;
        updateTier();
    }

    public boolean redeemPoints(int pointsToRedeem) {
        if (points >= pointsToRedeem) {
            points -= pointsToRedeem;
            updateTier();
            return true;
        }
        return false;
    }

    private void updateTier() {
        if (points >= 1000) {
            tier = LoyaltyTier.GOLD;
        } else if (points >= 500) {
            tier = LoyaltyTier.SILVER;
        } else {
            tier = LoyaltyTier.BRONZE;
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getPoints() {
        return points;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    /**
     * Calculates discount based on loyalty tier.
     */
    public double getTierDiscount() {
        return tier.getDiscountPercentage();
    }

    @Override
    public String toString() {
        return String.format("Customer: %s | Tier: %s | Points: %d | Discount: %.0f%%",
            customerId, tier, points, getTierDiscount());
    }
}

