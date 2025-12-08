package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.util.OptionalInt;
import java.util.function.BiFunction;

/**
 * Abstract base class for discount strategies
 * that require whole number quantities.
 */
public abstract class AbstractWholeNumberDiscountStrategy
        implements DiscountStrategy {

    /**
     * Checks if a BigDecimal value represents a whole number.
     *
     * @param value the value to check
     * @return true if the value is a whole number, false otherwise
     */
    protected final boolean isWholeNumber(final BigDecimal value) {
        return value.stripTrailingZeros().scale() <= 0;
    }

    /**
     * Validates and converts quantity to integer if it's a whole number
     * and meets the minimum requirement.
     *
     * @param quantity the quantity to validate
     * @param minimumRequired the minimum quantity required for discount
     * @return OptionalInt containing quantity as int if valid, empty otherwise
     */
    protected final OptionalInt validateAndConvertQuantity(
            final BigDecimal quantity,
            final int minimumRequired) {
        if (!isWholeNumber(quantity)) {
            return OptionalInt.empty();
        }

        final int quantityAsInt = quantity.intValue();

        if (quantityAsInt < minimumRequired) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(quantityAsInt);
    }

    /**
     * Calculates discount for set-based offers.
     *
     * @param product the product
     * @param quantity the quantity
     * @param unitPrice the unit price
     * @param itemsInSet number of items in a set
     * @param setPriceCalculator function to calculate price for one set
     * @param description the discount description
     * @return calculated Discount or null if quantity is invalid
     */
    protected final Discount calculateSetBasedDiscount(
            final Product product,
            final BigDecimal quantity,
            final BigDecimal unitPrice,
            final int itemsInSet,
            final BiFunction<Integer, BigDecimal, BigDecimal>
                    setPriceCalculator,
            final String description) {

        final var validQuantity =
                validateAndConvertQuantity(quantity, itemsInSet);
        if (validQuantity.isEmpty()) {
            return null;
        }

        final int quantityAsInt = validQuantity.getAsInt();
        final int numberOfSets = quantityAsInt / itemsInSet;
        final int remainder = quantityAsInt % itemsInSet;

        final BigDecimal totalWithDiscount = setPriceCalculator
                .apply(numberOfSets, unitPrice)
                .add(unitPrice.multiply(BigDecimal.valueOf(remainder)));

        final BigDecimal discountAmount = quantity.multiply(unitPrice)
                .subtract(totalWithDiscount);

        return new Discount(product, description, discountAmount.negate());
    }
}

