package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Buy 2 for a fixed amount offer strategy.
 */
public class TwoForAmountStrategy implements OfferStrategy {

    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt < 2) {
            return null;
        }

        double total = argument * (quantityAsInt / 2) + quantityAsInt % 2 * unitPrice;
        double discountAmount = unitPrice * quantity - total;
        return new Discount(product, "2 for " + argument, -discountAmount);
    }

    @Override
    public String getDescription() {
        return "2 for amount";
    }
}

