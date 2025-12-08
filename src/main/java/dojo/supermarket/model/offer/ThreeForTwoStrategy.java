package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ThreeForTwoStrategy implements DiscountStrategy {

    private static final int ITEMS_TO_BUY = 3;
    private static final int ITEMS_TO_PAY = 2;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final BigDecimal quantity,
                                       final BigDecimal unitPrice,
                                       final BigDecimal argument) {
        if (!isWholeNumber(quantity)) {
            return null;
        }

        final int quantityAsInt = quantity.intValue();

        if (quantityAsInt < ITEMS_TO_BUY) {
            return null;
        }

        final int numberOfSets = quantityAsInt / ITEMS_TO_BUY;
        final int remainder = quantityAsInt % ITEMS_TO_BUY;

        final BigDecimal totalWithDiscount = BigDecimal.valueOf(numberOfSets)
                .multiply(BigDecimal.valueOf(ITEMS_TO_PAY))
                .multiply(unitPrice)
                .add(BigDecimal.valueOf(remainder).multiply(unitPrice));

        final BigDecimal discountAmount = quantity.multiply(unitPrice)
                .subtract(totalWithDiscount);

        return new Discount(product, "3 for 2", discountAmount.negate());
    }

    private boolean isWholeNumber(final BigDecimal value) {
        return value.stripTrailingZeros().scale() <= 0;
    }
}
