package dojo.supermarket.model.loyalty;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a customer's loyalty card with credit points.
 * Points can be earned from purchases and used for payments.
 */
public final class LoyaltyCard {

	private final String cardNumber;
	private BigDecimal points;

    /**
     * Creates a loyalty card with zero points.
     *
     * @param cardNum the card number
     */
    public LoyaltyCard(final String cardNum) {
        this(cardNum, BigDecimal.ZERO);
    }

    /**
     * Creates a loyalty card with initial points.
     *
     * @param cardNum       the card number
     * @param initialPoints the initial points balance
     */
    public LoyaltyCard(final String cardNum,
                       final BigDecimal initialPoints) {
        this.cardNumber = Objects.requireNonNull(cardNum,
                "Card number cannot be null");
        this.points = Objects.requireNonNull(initialPoints,
                "Initial points cannot be null");

        if (initialPoints.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Initial points cannot be negative");
        }
    }

    /**
     * Adds points to the card.
     *
     * @param pointsToAdd the points to add
     */
    public void addPoints(final BigDecimal pointsToAdd) {
        if (pointsToAdd.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Cannot add negative points");
        }
        this.points = this.points.add(pointsToAdd);
    }

    /**
     * Deducts points from the card.
     *
     * @param pointsToUse the points to deduct
     * @return true if successful, false if insufficient points
     */
    public boolean usePoints(final BigDecimal pointsToUse) {
        if (pointsToUse.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Cannot use negative points");
        }
        if (pointsToUse.compareTo(this.points) > 0) {
            return false;
        }
        this.points = this.points.subtract(pointsToUse);
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
    public BigDecimal getPoints() {
        return points;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LoyaltyCard that = (LoyaltyCard) o;
        return Objects.equals(cardNumber, that.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }

    @Override
    public String toString() {
        return "LoyaltyCard{"
                + "cardNumber='" + cardNumber + '\''
                + ", points=" + points
                + '}';
    }
}

