package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Percentage discount offer strategy.
 */
public class PercentageDiscountStrategy implements OfferStrategy {

    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        double discountAmount = quantity * unitPrice * argument / 100.0;
        return new Discount(product, argument + "% off", -discountAmount);
    }

    @Override
    public String getDescription() {
        return "Percentage discount";
    }
}
