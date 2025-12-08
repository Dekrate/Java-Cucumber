package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageDiscountStrategy implements DiscountStrategy {

    /** Divisor for percentage calculations. */
    private static final BigDecimal PERCENTAGE_DIVISOR =
            new BigDecimal("100");
    /** Scale for BigDecimal rounding operations. */
    private static final int SCALE = 2;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final BigDecimal quantity,
                                       final BigDecimal unitPrice,
                                       final BigDecimal argument) {
        final BigDecimal totalPrice = quantity.multiply(unitPrice);
        final BigDecimal discountAmount = totalPrice
                .multiply(argument)
                .divide(PERCENTAGE_DIVISOR, SCALE, RoundingMode.HALF_UP);

        return new Discount(product,
                String.format("%s%% off", argument.stripTrailingZeros()
                        .toPlainString()),
                discountAmount.negate());
    }
}
