package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;

public final class ThreeForTwoStrategy
        extends AbstractWholeNumberDiscountStrategy {

    /** Number of items to buy for the offer. */
    private static final int ITEMS_TO_BUY = 3;
    /** Number of items to pay for. */
    private static final int ITEMS_TO_PAY = 2;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final BigDecimal quantity,
                                       final BigDecimal unitPrice,
                                       final BigDecimal argument) {
        return calculateSetBasedDiscount(
                product,
                quantity,
                unitPrice,
                ITEMS_TO_BUY,
                (sets, price) ->
                        price.multiply(BigDecimal
                                .valueOf((long) sets * ITEMS_TO_PAY)),
                "3 for 2"
        );
    }
}
