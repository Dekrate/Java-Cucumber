package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Manages loyalty program functionality.
 * Implements Service pattern for loyalty-related business logic.
 *
 * @param pointsPerCurrencyUnit points earned per currency unit spent
 * @param currencyPerPoint currency value per loyalty point
 */
public record LoyaltyProgramManager(BigDecimal pointsPerCurrencyUnit,
                                    BigDecimal currencyPerPoint) {

    /** Default points earned per currency unit. */
    private static final BigDecimal DEFAULT_POINTS_PER_CURRENCY_UNIT =
            BigDecimal.ONE;

    /** Default currency value per point. */
    private static final BigDecimal DEFAULT_CURRENCY_PER_POINT =
            new BigDecimal("0.01");

    /** Scale for BigDecimal rounding operations. */
    private static final int SCALE = 2;

    /**
     * Creates a loyalty program manager with default rates.
     */
    public LoyaltyProgramManager() {
        this(DEFAULT_POINTS_PER_CURRENCY_UNIT,
                DEFAULT_CURRENCY_PER_POINT);
    }

    /**
     * Calculates points earned from a purchase amount.
     *
     * @param amount the purchase amount
     * @return points earned
     */
    public BigDecimal calculatePointsEarned(final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative");
        }
        return amount.multiply(pointsPerCurrencyUnit)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Converts points to currency value.
     *
     * @param points the points to convert
     * @return currency value
     */
    public BigDecimal convertPointsToCurrency(final BigDecimal points) {
        if (points.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        }
        return points.multiply(currencyPerPoint)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Applies loyalty points as payment discount.
     *
     * @param card        the loyalty card
     * @param totalAmount the total purchase amount
     * @param pointsToUse points customer wants to use
     * @return Discount object if successful, null otherwise
     */
    public Discount applyLoyaltyPoints(final LoyaltyCard card,
                                       final BigDecimal totalAmount,
                                       final BigDecimal pointsToUse) {
        if (card == null) {
            return null;
        }

        if (pointsToUse.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        final BigDecimal maxPoints = totalAmount
                .divide(currencyPerPoint, SCALE, RoundingMode.HALF_UP);
        final BigDecimal actualPointsToUse = pointsToUse
                .min(maxPoints)
                .min(card.getPoints());

        if (actualPointsToUse.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        card.usePoints(actualPointsToUse);

        final BigDecimal discountAmount =
                convertPointsToCurrency(actualPointsToUse);

        final Product loyaltyProduct = new Product(
                "Loyalty Points Payment",
                ProductUnit.EACH);

        final String description = String.format(
                "Loyalty Points (%s points)",
                actualPointsToUse.stripTrailingZeros().toPlainString());

        return new Discount(loyaltyProduct, description,
                discountAmount.negate());
    }

    /**
     * Credits points to a loyalty card for a purchase.
     *
     * @param card   the loyalty card
     * @param amount the purchase amount (after all discounts)
     */
    public void creditPointsForPurchase(final LoyaltyCard card,
                                        final BigDecimal amount) {
        if (card != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            final BigDecimal pointsEarned = calculatePointsEarned(amount);
            card.addPoints(pointsEarned);
        }
    }
}

