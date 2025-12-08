package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.SupermarketCatalog;

import java.util.Map;

/**
 * Manages bundle discounts and calculates applicable discounts.
 * Uses Service pattern to encapsulate business logic.
 */
public final class BundleDiscountCalculator {

    /** Divisor to convert percentage to decimal. */
    private static final double PERCENTAGE_DIVISOR = 100.0;

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
            final Map<Product, Double> productQuantities,
            final SupermarketCatalog catalog) {
        final int completeBundles =
                countCompleteBundles(bundle, productQuantities);

        if (completeBundles == 0) {
            return null;
        }

        final double bundlePrice = calculateBundlePrice(bundle, catalog);
        final double discountAmount = bundlePrice
                * bundle.discountPercentage() / PERCENTAGE_DIVISOR
                * completeBundles;

        final Product bundleProduct = createBundleProduct(bundle);
        final String description = String.format("%s (%.0f%% off)",
                bundle.name(), bundle.discountPercentage());

        return new Discount(bundleProduct, description, -discountAmount);
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
            final Map<Product, Double> productQuantities) {
        int minBundles = Integer.MAX_VALUE;

        for (Map.Entry<Product, Integer> entry
                : bundle.requiredProducts().entrySet()) {
            final Product product = entry.getKey();
            final int requiredQuantity = entry.getValue();

            final double availableQuantity =
                    productQuantities.getOrDefault(product, 0.0);
            final int possibleBundles =
                    (int) (availableQuantity / requiredQuantity);

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
    private double calculateBundlePrice(final ProductBundle bundle,
                                        final SupermarketCatalog catalog) {
        double totalPrice = 0.0;

        for (Map.Entry<Product, Integer> entry
                : bundle.requiredProducts().entrySet()) {
            final Product product = entry.getKey();
            final int quantity = entry.getValue();
            final double unitPrice = catalog.getUnitPrice(product);

            totalPrice += unitPrice * quantity;
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

