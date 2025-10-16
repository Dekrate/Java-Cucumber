package dojo.supermarket.model;

/**
 * Represents a product category with specific business rules.
 * Implementations define category-specific behavior such as quality degradation,
 * pricing adjustments, or special handling rules.
 *
 * This interface enables the Open/Closed Principle by allowing new categories
 * to be added without modifying existing code.
 */
public interface ProductCategory {

    /**
     * Gets the category name for display purposes.
     */
    String getCategoryName();

    /**
     * Applies category-specific price adjustment to the base price.
     * For example, conjured items might have different pricing rules.
     *
     * @param basePrice the base price from catalog
     * @param quantity the quantity being purchased
     * @return the adjusted price
     */
    double applyPriceAdjustment(double basePrice, double quantity);

    /**
     * Determines if this category has special bundling rules.
     */
    default boolean hasBundleRules() {
        return false;
    }
}

