package dojo.supermarket.model.category;

import dojo.supermarket.model.ProductCategory;

/**
 * Premium product category for high-quality items.
 * These items might have special handling or pricing rules.
 */
public class PremiumCategory implements ProductCategory {

    @Override
    public String getCategoryName() {
        return "Premium";
    }

    @Override
    public double applyPriceAdjustment(double basePrice, double quantity) {
        return basePrice * quantity;
    }
}

