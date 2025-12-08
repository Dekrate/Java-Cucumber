package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

public final class PercentageDiscountStrategy
        implements DiscountStrategy {

    /** Divisor to convert percentage to decimal. */
    private static final double PERCENTAGE_DIVISOR = 100.0;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final double quantity,
                                       final double unitPrice,
                                       final double argument) {
        final double totalPrice = quantity * unitPrice;
        final double discountAmount =
                totalPrice * argument / PERCENTAGE_DIVISOR;

        return new Discount(product,
                String.format("%.0f%% off", argument),
                -discountAmount);
    }
}
