package dojo.supermarket.model.coupon;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.SupermarketCatalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages coupon validation and discount calculation.
 * Implements Service pattern for coupon-related business logic.
 */
public class CouponManager {

    /**
     * Divisor to convert percentage to decimal.
     */
    private static final double PERCENTAGE_DIVISOR = 100.0;

    /**
     * List of available coupons.
     */
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
            final Map<Product, Double> productQuantities,
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
            final Map<Product, Double> productQuantities,
            final SupermarketCatalog catalog) {
        final Product product = coupon.getProduct();
        final double availableQuantity =
                productQuantities.getOrDefault(product, 0.0);

        final int totalRequired = coupon.getRequiredQuantity()
                + coupon.getDiscountedQuantity();
        if (availableQuantity < totalRequired) {
            return null;
        }

        final double unitPrice = catalog.getUnitPrice(product);
        final double discountAmount = unitPrice
                * coupon.getDiscountedQuantity()
                * coupon.getDiscountPercentage()
                / PERCENTAGE_DIVISOR;

        final String description = String.format(
                "Coupon %s: Buy %d get %d at %.0f%% off",
                coupon.getCode(),
                coupon.getRequiredQuantity(),
                coupon.getDiscountedQuantity(),
                coupon.getDiscountPercentage());

        return new Discount(product, description, -discountAmount);
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
     * Removes expired coupons based on the given date.
     *
     * @param currentDate the current date
     */
    public void removeExpiredCoupons(final LocalDate currentDate) {
        availableCoupons.removeIf(coupon ->
            currentDate.isAfter(coupon.getValidUntil()));
    }
}

