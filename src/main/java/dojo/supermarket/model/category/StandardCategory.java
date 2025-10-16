package dojo.supermarket.model.category;

import dojo.supermarket.model.ProductCategory;

/**
 * Standard product category with no special rules.
 * Applies normal pricing without adjustments.
 */
public class StandardCategory implements ProductCategory {

    @Override
    public String getCategoryName() {
        return "Standard";
    }

    @Override
    public double applyPriceAdjustment(double basePrice, double quantity) {
        return basePrice * quantity;
    }
}

