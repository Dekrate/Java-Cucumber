package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;

public final class FiveForAmountStrategy implements DiscountStrategy {

    private static final int ITEMS_IN_SET = 5;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final BigDecimal quantity,
                                       final BigDecimal unitPrice,
                                       final BigDecimal argument) {
        if (!isWholeNumber(quantity)) {
            return null;
        }

        final int quantityAsInt = quantity.intValue();

        if (quantityAsInt < ITEMS_IN_SET) {
            return null;
        }

        final int numberOfSets = quantityAsInt / ITEMS_IN_SET;
        final int remainder = quantityAsInt % ITEMS_IN_SET;

        final BigDecimal totalWithDiscount = argument
                .multiply(BigDecimal.valueOf(numberOfSets))
                .add(unitPrice.multiply(BigDecimal.valueOf(remainder)));

        final BigDecimal discountAmount = quantity.multiply(unitPrice)
                .subtract(totalWithDiscount);

        return new Discount(product,
                String.format("%d for %s", ITEMS_IN_SET,
                        argument.toPlainString()),
                discountAmount.negate());
    }

    private boolean isWholeNumber(final BigDecimal value) {
        return value.stripTrailingZeros().scale() <= 0;
    }
}
