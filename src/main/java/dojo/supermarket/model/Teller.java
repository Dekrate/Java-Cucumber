package dojo.supermarket.model;

import dojo.supermarket.model.bundle.BundleDiscountCalculator;
import dojo.supermarket.model.bundle.ProductBundle;
import dojo.supermarket.model.coupon.Coupon;
import dojo.supermarket.model.coupon.CouponManager;
import dojo.supermarket.model.loyalty.LoyaltyCard;
import dojo.supermarket.model.loyalty.LoyaltyProgramManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles checkout process and applies discounts.
 * Orchestrates discount calculations from various sources.
 */
public class Teller {

    /**
     * The supermarket catalog for price lookup.
     */
    private final SupermarketCatalog catalog;

    /**
     * Map of products to their special offers.
     */
    private final Map<Product, Offer> offers = new HashMap<>();

    /**
     * List of product bundles for discount consideration.
     */
    private final List<ProductBundle> bundles = new ArrayList<>();

    /**
     * Calculator for bundle discounts.
     */
    private final BundleDiscountCalculator bundleCalculator =
            new BundleDiscountCalculator();

    /**
     * Manager for coupon discounts.
     */
    private final CouponManager couponManager = new CouponManager();

    /**
     * Manager for loyalty program.
     */
    private final LoyaltyProgramManager loyaltyManager =
            new LoyaltyProgramManager();

    /**
     * The purchase date for coupon validity checks.
     */
    private LocalDate purchaseDate = LocalDate.now();

    /**
     * Creates a new Teller.
     *
     * @param cat the supermarket catalog
     */
    public Teller(final SupermarketCatalog cat) {
        this.catalog = cat;
    }

    /**
     * Adds a special offer.
     *
     * @param offerType the offer type
     * @param product   the product
     * @param argument  the offer argument
     */
    public void addSpecialOffer(final SpecialOfferType offerType,
                                final Product product,
                                final double argument) {
        offers.put(product, new Offer(offerType, product, argument));
    }

    /**
     * Adds a product bundle for discount consideration.
     *
     * @param bundle the product bundle to add
     */
    public void addProductBundle(final ProductBundle bundle) {
        bundles.add(bundle);
    }

    /**
     * Adds a coupon for discount consideration.
     *
     * @param coupon the coupon to add
     */
    public void addCoupon(final Coupon coupon) {
        couponManager.addCoupon(coupon);
    }

    /**
     * Processes checkout and calculates all applicable discounts.
     * Order of application: bundles, coupons, then regular offers.
     *
     * @param theCart the shopping cart
     * @return receipt with all items and discounts
     */
    public Receipt checksOutArticlesFrom(final ShoppingCart theCart) {
        return checksOutArticlesFrom(theCart, null, 0.0);
    }

    /**
     * Processes checkout with loyalty card support.
     * Applies discounts and optionally uses loyalty points for payment.
     * Credits earned points to the loyalty card after purchase.
     *
     * @param theCart      the shopping cart
     * @param loyaltyCard  the customer's loyalty card (can be null)
     * @param pointsToUse  the number of points to use for payment
     * @return receipt with all items and discounts
     */
    public Receipt checksOutArticlesFrom(final ShoppingCart theCart,
                                         final LoyaltyCard loyaltyCard,
                                         final double pointsToUse) {
        final Receipt receipt = new Receipt();
        final List<ProductQuantity> productQuantities =
                theCart.getItems();

        for (ProductQuantity pq: productQuantities) {
            final Product p = pq.product();
            final double quantity = pq.quantity();
            final double unitPrice = catalog.getUnitPrice(p);
            final double price = quantity * unitPrice;
            receipt.addProduct(p, quantity, unitPrice, price);
        }

        final Map<Product, Double> remainingQuantities =
                new HashMap<>(theCart.productQuantities());

        for (ProductBundle bundle : bundles) {
            final Discount bundleDiscount =
                    bundleCalculator.calculateBundleDiscount(
                            bundle, remainingQuantities, catalog);

            if (bundleDiscount != null) {
                receipt.addDiscount(bundleDiscount);
            }
        }

        final List<Discount> couponDiscounts =
                couponManager.applyCoupons(
                        remainingQuantities, catalog, purchaseDate);
        couponDiscounts.forEach(receipt::addDiscount);

        theCart.handleOffers(receipt, offers, catalog);

        final double totalBeforeLoyalty = receipt.getTotalPrice();

        if (loyaltyCard != null && pointsToUse > 0) {
            final Discount loyaltyDiscount =
                    loyaltyManager.applyLoyaltyPoints(
                            loyaltyCard, totalBeforeLoyalty, pointsToUse);
            if (loyaltyDiscount != null) {
                receipt.addDiscount(loyaltyDiscount);
            }
        }

        if (loyaltyCard != null) {
            final double finalTotal = receipt.getTotalPrice();
            loyaltyManager.creditPointsForPurchase(loyaltyCard,
                    finalTotal);
        }

        return receipt;
    }

    /**
     * Sets the purchase date (useful for testing coupon validity).
     *
     * @param date the purchase date to set
     */
    public void setPurchaseDate(final LocalDate date) {
        this.purchaseDate = date;
    }
}
