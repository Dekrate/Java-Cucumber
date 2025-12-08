package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;

public final class FiveForAmountStrategy
        extends AbstractWholeNumberDiscountStrategy {

    /** Number of items required for the discount. */
    private static final int ITEMS_IN_SET = 5;

    @Override
    public Discount calculateDiscount(final Product product,
                                       final BigDecimal quantity,
                                       final BigDecimal unitPrice,
                                       final BigDecimal argument) {
        return calculateSetBasedDiscount(
                product,
                quantity,
                unitPrice,
                ITEMS_IN_SET,
                (sets, _) ->
                        argument.multiply(BigDecimal.valueOf(sets)),
                String.format("%d for %s", ITEMS_IN_SET,
                        argument.toPlainString())
        );
    }
}
