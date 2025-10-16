package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Buy 3, pay for 2 offer strategy.
 */
public class ThreeForTwoStrategy implements OfferStrategy {

    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt <= 2) {
            return null;
        }

        int numberOfSets = quantityAsInt / 3;
        double discountAmount = quantity * unitPrice - ((numberOfSets * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
        return new Discount(product, "3 for 2", -discountAmount);
    }

    @Override
    public String getDescription() {
        return "3 for 2";
    }
}

