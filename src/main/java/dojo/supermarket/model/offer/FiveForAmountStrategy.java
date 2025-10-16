package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Buy 5 for a fixed amount offer strategy.
 */
public class FiveForAmountStrategy implements OfferStrategy {

    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt < 5) {
            return null;
        }

        int numberOfSets = quantityAsInt / 5;
        double discountTotal = unitPrice * quantity - (argument * numberOfSets + quantityAsInt % 5 * unitPrice);
        return new Discount(product, "5 for " + argument, -discountTotal);
    }

    @Override
    public String getDescription() {
        return "5 for amount";
    }
}

