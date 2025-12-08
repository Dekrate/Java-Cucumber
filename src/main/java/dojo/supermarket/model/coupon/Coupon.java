package dojo.supermarket.model.coupon;

import dojo.supermarket.model.Product;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a coupon that provides a discount on specific products.
 * Coupons have validity periods and can only be used once.
 * Example: Buy 6 bottles, get 6 more at half price (valid 13/11 - 15/11).
 */
public final class Coupon {

    /**
     * Maximum allowed discount percentage.
     */
    private static final double MAX_DISCOUNT_PERCENTAGE = 100.0;

    /**
     * Unique coupon code.
     */
    private final String code;

    /**
     * Product this coupon applies to.
     */
    private final Product product;

    /**
     * Minimum quantity required to activate coupon.
     */
    private final int requiredQuantity;

    /**
     * Quantity that receives the discount.
     */
    private final int discountedQuantity;

    /**
     * Discount percentage (0-100).
     */
    private final double discountPercentage;

    /**
     * Start date of validity.
     */
    private final LocalDate validFrom;

    /**
     * End date of validity (inclusive).
     */
    private final LocalDate validUntil;

    /**
     * Whether coupon has been redeemed.
     */
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
                  final double discPercentage,
                  final LocalDate validFromDate,
                  final LocalDate validUntilDate) {
        this.code = Objects.requireNonNull(couponCode,
                "Coupon code cannot be null");
        this.product = Objects.requireNonNull(couponProduct,
                "Product cannot be null");
        this.requiredQuantity = reqQuantity;
        this.discountedQuantity = discQuantity;
        this.discountPercentage = discPercentage;
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
        if (discountPercentage < 0
                || discountPercentage > MAX_DISCOUNT_PERCENTAGE) {
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
    public double getDiscountPercentage() {
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
     * @return true if redeemed, false otherwise
     */
    public boolean isRedeemed() {
        return redeemed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Coupon coupon)) {
            return false;
        }
        return Objects.equals(code, coupon.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format(
                "Coupon{code='%s', product=%s, %.0f%%%% off, "
                        + "valid %s to %s, redeemed=%s}",
                code, product.name(), discountPercentage,
                validFrom, validUntil, redeemed);
    }
}

