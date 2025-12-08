package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Strategy interface for calculating discounts.
 */
public interface DiscountStrategy {
    /**
     * Calculates discount for given parameters.
     *
     * @param product   the product
     * @param quantity  the quantity
     * @param unitPrice the unit price
     * @param argument  the strategy-specific argument
     * @return discount or null if not applicable
     */
    Discount calculateDiscount(Product product,
                               double quantity,
                               double unitPrice,
                               double argument);
}
