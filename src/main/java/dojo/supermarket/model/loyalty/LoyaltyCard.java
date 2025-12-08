package dojo.supermarket.model.loyalty;

import java.util.Objects;

/**
 * Represents a customer's loyalty card with credit points.
 * Points can be earned from purchases and used for payments.
 */
public final class LoyaltyCard {

    /**
     * The card number.
     */
    private final String cardNumber;

    /**
     * The points balance.
     */
    private double points;

    /**
     * Creates a loyalty card with zero points.
     *
     * @param cardNum the card number
     */
    public LoyaltyCard(final String cardNum) {
        this(cardNum, 0.0);
    }

    /**
     * Creates a loyalty card with initial points.
     *
     * @param cardNum       the card number
     * @param initialPoints the initial points balance
     */
    public LoyaltyCard(final String cardNum,
                       final double initialPoints) {
        this.cardNumber = Objects.requireNonNull(cardNum,
                "Card number cannot be null");
        this.points = initialPoints;

        if (initialPoints < 0) {
            throw new IllegalArgumentException(
                    "Initial points cannot be negative");
        }
    }

    /**
     * Adds points to the card.
     *
     * @param pointsToAdd the points to add
     */
    public void addPoints(final double pointsToAdd) {
        if (pointsToAdd < 0) {
            throw new IllegalArgumentException(
                    "Cannot add negative points");
        }
        this.points += pointsToAdd;
    }

    /**
     * Deducts points from the card.
     *
     * @param pointsToUse the points to deduct
     * @return true if successful, false if insufficient points
     */
    public boolean usePoints(final double pointsToUse) {
        if (pointsToUse < 0) {
            throw new IllegalArgumentException(
                    "Cannot use negative points");
        }
        if (pointsToUse > this.points) {
            return false;
        }
        this.points -= pointsToUse;
        return true;
    }

    /**
     * Gets the card number.
     *
     * @return the card number
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Gets the points balance.
     *
     * @return the points balance
     */
    public double getPoints() {
        return points;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoyaltyCard that)) {
            return false;
        }
        return Objects.equals(cardNumber, that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return String.format(
                "LoyaltyCard{cardNumber='%s', points=%.2f}",
                cardNumber, points);
    }
}

