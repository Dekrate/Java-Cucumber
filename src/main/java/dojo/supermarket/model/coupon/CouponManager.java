package dojo.supermarket.model.coupon;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.SupermarketCatalog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages coupon validation and discount calculation.
 * Implements Service pattern for coupon-related business logic.
 */
public final class CouponManager {

    /** Divisor for percentage calculations. */
    private static final BigDecimal PERCENTAGE_DIVISOR =
            new BigDecimal("100");
    /** Scale for BigDecimal rounding operations. */
    private static final int SCALE = 2;

    /** List of available coupons. */
    private final List<Coupon> availableCoupons = new ArrayList<>();

    /**
     * Adds a coupon to the manager.
     *
     * @param coupon the coupon to add
     */
    public void addCoupon(final Coupon coupon) {
        availableCoupons.add(coupon);
    }

    /**
     * Applies valid coupons for the given products and date.
     *
     * @param productQuantities map of products to quantities
     * @param catalog           the catalog for price lookup
     * @param purchaseDate      the date of purchase
     * @return list of applicable discounts
     */
    public List<Discount> applyCoupons(
            final Map<Product, BigDecimal> productQuantities,
            final SupermarketCatalog catalog,
            final LocalDate purchaseDate) {
        final List<Discount> discounts = new ArrayList<>();

        for (Coupon coupon : availableCoupons) {
            if (coupon.isValidOn(purchaseDate)) {
                final Discount discount = tryApplyCoupon(
                        coupon, productQuantities, catalog);
                if (discount != null) {
                    discounts.add(discount);
                    coupon.redeem();
                }
            }
        }

        return discounts;
    }

    /**
     * Attempts to apply a single coupon.
     *
     * @param coupon            the coupon to apply
     * @param productQuantities map of products to quantities
     * @param catalog           the catalog for price lookup
     * @return Discount if applicable, null otherwise
     */
    private Discount tryApplyCoupon(
            final Coupon coupon,
            final Map<Product, BigDecimal> productQuantities,
            final SupermarketCatalog catalog) {
        final Product product = coupon.getProduct();
        final BigDecimal availableQuantity =
                productQuantities.getOrDefault(product, BigDecimal.ZERO);

        final int totalRequired = coupon.getRequiredQuantity()
                + coupon.getDiscountedQuantity();
        if (availableQuantity.compareTo(
                BigDecimal.valueOf(totalRequired)) < 0) {
            return null;
        }

        final BigDecimal unitPrice = catalog.getUnitPrice(product);
        final BigDecimal discountAmount = unitPrice
                .multiply(BigDecimal.valueOf(coupon.getDiscountedQuantity()))
                .multiply(coupon.getDiscountPercentage())
                .divide(PERCENTAGE_DIVISOR, SCALE, RoundingMode.HALF_UP);

        final String description = String.format(
                "Coupon %s: Buy %d get %d at %s%% off",
                coupon.getCode(),
                coupon.getRequiredQuantity(),
                coupon.getDiscountedQuantity(),
                coupon.getDiscountPercentage().stripTrailingZeros()
                        .toPlainString());

        return new Discount(product, description, discountAmount.negate());
    }

    /**
     * Removes all coupons.
     */
    public void clearCoupons() {
        availableCoupons.clear();
    }

    /**
     * Gets all available (not redeemed) coupons.
     *
     * @return list of available coupons
     */
    public List<Coupon> getAvailableCoupons() {
        return availableCoupons.stream()
                .filter(c -> !c.isRedeemed())
                .toList();
    }

    /**
     * Checks if the manager has any available coupons.
     *
     * @return true if there are available coupons
     */
    public boolean hasAvailableCoupons() {
        return availableCoupons.stream().anyMatch(c -> !c.isRedeemed());
    }
}

