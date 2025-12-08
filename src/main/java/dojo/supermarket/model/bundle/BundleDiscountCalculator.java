package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.SupermarketCatalog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Manages bundle discounts and calculates applicable discounts.
 * Uses Service pattern to encapsulate business logic.
 */
public final class BundleDiscountCalculator {

    private static final BigDecimal PERCENTAGE_DIVISOR =
            new BigDecimal("100");
    private static final int SCALE = 2;

    /**
     * Calculates the bundle discount for purchased products.
     * Only complete bundles receive discounts.
     *
     * @param bundle             the product bundle
     * @param productQuantities  map of products to quantities
     * @param catalog            the catalog for price lookup
     * @return Discount object if bundle complete, null otherwise
     */
    public Discount calculateBundleDiscount(
            final ProductBundle bundle,
            final Map<Product, BigDecimal> productQuantities,
            final SupermarketCatalog catalog) {
        final int completeBundles =
                countCompleteBundles(bundle, productQuantities);

        if (completeBundles == 0) {
            return null;
        }

        final BigDecimal bundlePrice = calculateBundlePrice(bundle, catalog);
        final BigDecimal discountAmount = bundlePrice
                .multiply(bundle.discountPercentage())
                .divide(PERCENTAGE_DIVISOR, SCALE, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(completeBundles));

        final Product bundleProduct = createBundleProduct(bundle);
        final String description = String.format("%s (%s%% off)",
                bundle.name(),
                bundle.discountPercentage().stripTrailingZeros()
                        .toPlainString());

        return new Discount(bundleProduct, description,
                discountAmount.negate());
    }

    /**
     * Counts complete bundles from cart contents.
     *
     * @param bundle            the product bundle
     * @param productQuantities map of products to quantities
     * @return number of complete bundles
     */
    private int countCompleteBundles(
            final ProductBundle bundle,
            final Map<Product, BigDecimal> productQuantities) {
        int minBundles = Integer.MAX_VALUE;

        for (Map.Entry<Product, Integer> entry
                : bundle.requiredProducts().entrySet()) {
            final Product product = entry.getKey();
            final int requiredQuantity = entry.getValue();

            final BigDecimal availableQuantity =
                    productQuantities.getOrDefault(product, BigDecimal.ZERO);
            final int possibleBundles =
                    availableQuantity.intValue() / requiredQuantity;

            minBundles = Math.min(minBundles, possibleBundles);
        }

        return minBundles == Integer.MAX_VALUE ? 0 : minBundles;
    }

    /**
     * Calculates the total price of one bundle.
     *
     * @param bundle  the product bundle
     * @param catalog the catalog for price lookup
     * @return total price of the bundle
     */
    private BigDecimal calculateBundlePrice(final ProductBundle bundle,
                                            final SupermarketCatalog catalog) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<Product, Integer> entry
                : bundle.requiredProducts().entrySet()) {
            final Product product = entry.getKey();
            final int quantity = entry.getValue();
            final BigDecimal unitPrice = catalog.getUnitPrice(product);

            totalPrice = totalPrice.add(
                    unitPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        return totalPrice;
    }

    /**
     * Creates a synthetic product to represent the bundle.
     *
     * @param bundle the product bundle
     * @return a Product representing the bundle
     */
    private Product createBundleProduct(final ProductBundle bundle) {
        return new Product("Bundle: " + bundle.name(),
                          dojo.supermarket.model.ProductUnit.EACH);
    }
}

