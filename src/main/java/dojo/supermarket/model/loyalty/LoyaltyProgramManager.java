package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;

/**
 * Manages loyalty program functionality.
 * Implements Service pattern for loyalty-related business logic.
 *
 * @param pointsPerCurrencyUnit -- GETTER --
 *                              Gets points per currency unit.
 * @param currencyPerPoint      -- GETTER --
 *                              Gets currency per point.
 */
public record LoyaltyProgramManager(double pointsPerCurrencyUnit,
                                    double currencyPerPoint) {

    /**
     * Default points per currency unit.
     */
    private static final double DEFAULT_POINTS_PER_CURRENCY_UNIT = 1.0;

    /**
     * Default currency per point.
     */
    private static final double DEFAULT_CURRENCY_PER_POINT = 0.01;

    /**
     * Creates a loyalty program manager with default rates.
     */
    public LoyaltyProgramManager() {
        this(DEFAULT_POINTS_PER_CURRENCY_UNIT,
                DEFAULT_CURRENCY_PER_POINT);
    }

    /**
     * Creates a loyalty program manager with custom rates.
     *
     * @param pointsPerCurrencyUnit points earned per currency unit
     * @param currencyPerPoint      currency value of one point
     */
    public LoyaltyProgramManager {
    }

    /**
     * Calculates points earned from a purchase amount.
     *
     * @param amount the purchase amount
     * @return points earned
     */
    public double calculatePointsEarned(final double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative");
        }
        return amount * pointsPerCurrencyUnit;
    }

    /**
     * Converts points to currency value.
     *
     * @param points the points to convert
     * @return currency value
     */
    public double convertPointsToCurrency(final double points) {
        if (points < 0) {
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        }
        return points * currencyPerPoint;
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
                                       final double totalAmount,
                                       final double pointsToUse) {
        if (card == null) {
            return null;
        }

        if (pointsToUse <= 0) {
            return null;
        }

        final double maxPoints = totalAmount / currencyPerPoint;
        final double actualPointsToUse = Math.min(pointsToUse,
                Math.min(maxPoints, card.getPoints()));

        if (actualPointsToUse <= 0) {
            return null;
        }

        if (!card.usePoints(actualPointsToUse)) {
            return null;
        }

        final double discountAmount =
                convertPointsToCurrency(actualPointsToUse);

        final Product loyaltyProduct = new Product(
                "Loyalty Points Payment",
                ProductUnit.EACH);

        final String description = String.format(
                "Loyalty Points (%.0f points)", actualPointsToUse);

        return new Discount(loyaltyProduct, description,
                -discountAmount);
    }

    /**
     * Credits points to a loyalty card for a purchase.
     *
     * @param card   the loyalty card
     * @param amount the purchase amount (after all discounts)
     */
    public void creditPointsForPurchase(final LoyaltyCard card,
                                        final double amount) {
        if (card != null && amount > 0) {
            final double pointsEarned = calculatePointsEarned(amount);
            card.addPoints(pointsEarned);
        }
    }

}

