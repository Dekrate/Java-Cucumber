package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

public final class ThreeForTwoStrategy implements DiscountStrategy {

    /**
     * Number of items required for the offer.
     */
    private static final int ITEMS_TO_BUY = 3;

    /**
     * Number of items to pay for in the offer.
     */
    private static final int ITEMS_TO_PAY = 2;

    /**
     * Epsilon for floating-point comparisons.
     */
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

        if (quantityAsInt < ITEMS_TO_BUY) {
            return null;
        }

        final int numberOfSets = quantityAsInt / ITEMS_TO_BUY;
        final int remainder = quantityAsInt % ITEMS_TO_BUY;

        final double totalWithDiscount =
                (numberOfSets * ITEMS_TO_PAY * unitPrice)
                        + (remainder * unitPrice);
        final double discountAmount =
                quantity * unitPrice - totalWithDiscount;

        return new Discount(product, "3 for 2", -discountAmount);
    }

    private boolean isWholeNumber(final double value) {
        return Math.abs(value - Math.round(value)) < EPSILON;
    }
}
