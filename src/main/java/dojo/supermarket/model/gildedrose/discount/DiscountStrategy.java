package dojo.supermarket.model.gildedrose.discount;

/**
 * Strategy interface for calculating discounts.
 * Implementations can define different discount calculation methods.
 */
public interface DiscountStrategy {
    /**
     * Calculates the discount amount for a given base price.
     *
     * @param basePrice the original price before discount
     * @return the discount amount to be subtracted from base price
     */
    double calculateDiscount(double basePrice);

    /**
     * Gets a description of this discount strategy.
     *
     * @return description of the discount
     */
    String getDescription();
}

