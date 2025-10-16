package dojo.supermarket.model.category;

import dojo.supermarket.model.ProductCategory;

/**
 * Conjured items category - items with magical properties.
 * These items degrade in quality twice as fast as normal items,
 * which is reflected in their pricing structure.
 */
public class ConjuredCategory implements ProductCategory {

    private final double degradationMultiplier;

    public ConjuredCategory() {
        this.degradationMultiplier = 2.0;
    }

    @Override
    public String getCategoryName() {
        return "Conjured";
    }

    @Override
    public double applyPriceAdjustment(double basePrice, double quantity) {
        // Conjured items maintain standard pricing
        // The degradation factor affects quality, not immediate price
        return basePrice * quantity;
    }

    public double getDegradationMultiplier() {
        return degradationMultiplier;
    }
}

