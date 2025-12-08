package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

public final class FiveForAmountStrategy implements DiscountStrategy {

    /** Number of items required in a set. */
    private static final int ITEMS_IN_SET = 5;
    /** Epsilon for floating point comparison. */
    private static final double EPSILON = 0.0001;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final double quantity,
                                       final double unitPrice,
                                       final double argument) {
        if (!isWholeNumber(quantity)) {
            return null;
        }

        final int quantityAsInt = (int) Math.round(quantity);

        if (quantityAsInt < ITEMS_IN_SET) {
            return null;
        }

        final int numberOfSets = quantityAsInt / ITEMS_IN_SET;
        final int remainder = quantityAsInt % ITEMS_IN_SET;

        final double totalWithDiscount =
                (numberOfSets * argument) + (remainder * unitPrice);
        final double discountAmount =
                quantity * unitPrice - totalWithDiscount;

        return new Discount(product,
                String.format("%d for %.2f", ITEMS_IN_SET, argument),
                -discountAmount);
    }

    private boolean isWholeNumber(final double value) {
        return Math.abs(value - Math.round(value)) < EPSILON;
    }
}
