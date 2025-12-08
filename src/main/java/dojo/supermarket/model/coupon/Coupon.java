package dojo.supermarket.model.coupon;

import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a coupon that provides a discount on specific products.
 * Coupons have validity periods and can only be used once.
 * Example: Buy 6 bottles, get 6 more at half price (valid 13/11 - 15/11).
 */
public final class Coupon {

    /** Maximum allowed discount percentage. */
    private static final BigDecimal MAX_DISCOUNT_PERCENTAGE =
            new BigDecimal("100.0");

    /** The coupon code. */
    private final String code;
    /** The product this coupon applies to. */
    private final Product product;
    /** The required quantity to trigger the coupon. */
    private final int requiredQuantity;
    /** The quantity that receives the discount. */
    private final int discountedQuantity;
    /** The discount percentage. */
    private final BigDecimal discountPercentage;
    /** The start date of validity. */
    private final LocalDate validFrom;
    /** The end date of validity (inclusive). */
    private final LocalDate validUntil;
    /** Whether the coupon has been redeemed. */
    private boolean redeemed;

    /**
     * Creates a new coupon.
     *
     * @param couponCode           unique coupon code
     * @param couponProduct        the product this coupon applies to
     * @param reqQuantity          minimum quantity required
     * @param discQuantity         quantity that receives discount
     * @param discPercentage       discount percentage (0-100)
     * @param validFromDate        start date of validity
     * @param validUntilDate       end date of validity (inclusive)
     */
    public Coupon(final String couponCode, final Product couponProduct,
                  final int reqQuantity,
                  final int discQuantity,
                  final BigDecimal discPercentage,
                  final LocalDate validFromDate,
                  final LocalDate validUntilDate) {
        this.code = Objects.requireNonNull(couponCode,
                "Coupon code cannot be null");
        this.product = Objects.requireNonNull(couponProduct,
                "Product cannot be null");
        this.requiredQuantity = reqQuantity;
        this.discountedQuantity = discQuantity;
        this.discountPercentage = Objects.requireNonNull(discPercentage,
                "Discount percentage cannot be null");
        this.validFrom = Objects.requireNonNull(validFromDate,
                "Valid from date cannot be null");
        this.validUntil = Objects.requireNonNull(validUntilDate,
                "Valid until date cannot be null");
        this.redeemed = false;

        validateParameters();
    }

    private void validateParameters() {
        if (requiredQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Required quantity must be positive");
        }
        if (discountedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Discounted quantity must be positive");
        }
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0
                || discountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100");
        }
        if (validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException(
                    "Valid until must not be before valid from");
        }
    }

    /**
     * Checks if the coupon is valid on the given date.
     *
     * @param date the date to check
     * @return true if coupon is valid and not redeemed
     */
    public boolean isValidOn(final LocalDate date) {
        return !redeemed
            && !date.isBefore(validFrom)
            && !date.isAfter(validUntil);
    }

    /**
     * Marks the coupon as redeemed.
     *
     * @throws IllegalStateException if coupon is already redeemed
     */
    public void redeem() {
        if (redeemed) {
            throw new IllegalStateException(
                    "Coupon has already been redeemed");
        }
        redeemed = true;
    }

    /**
     * Gets the coupon code.
     *
     * @return the unique coupon code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the product this coupon applies to.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the minimum required quantity.
     *
     * @return the required quantity
     */
    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    /**
     * Gets the quantity that receives the discount.
     *
     * @return the discounted quantity
     */
    public int getDiscountedQuantity() {
        return discountedQuantity;
    }

    /**
     * Gets the discount percentage.
     *
     * @return the discount percentage (0-100)
     */
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Gets the start date of validity.
     *
     * @return the start date
     */
    public LocalDate getValidFrom() {
        return validFrom;
    }

    /**
     * Gets the end date of validity.
     *
     * @return the end date (inclusive)
     */
    public LocalDate getValidUntil() {
        return validUntil;
    }

    /**
     * Checks if the coupon has been redeemed.
     *
     * @return true if redeemed
     */
    public boolean isRedeemed() {
        return redeemed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Coupon coupon = (Coupon) o;
        return Objects.equals(code, coupon.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Coupon{"
                + "code='" + code + '\''
                + ", product=" + product
                + ", requiredQuantity=" + requiredQuantity
                + ", discountedQuantity=" + discountedQuantity
                + ", discountPercentage=" + discountPercentage
                + ", validFrom=" + validFrom
                + ", validUntil=" + validUntil
                + ", redeemed=" + redeemed
                + '}';
    }
}

