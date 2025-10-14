package com.gildedrose.loyalty;

/**
 * Represents different tiers in the loyalty program.
 */
public enum LoyaltyTier {
    BRONZE(0, 5.0),
    SILVER(500, 10.0),
    GOLD(1000, 15.0);

    private final int pointsRequired;
    private final double discountPercentage;

    LoyaltyTier(int pointsRequired, double discountPercentage) {
        this.pointsRequired = pointsRequired;
        this.discountPercentage = discountPercentage;
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}

