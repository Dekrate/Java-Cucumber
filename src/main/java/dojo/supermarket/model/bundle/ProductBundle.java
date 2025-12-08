package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Product;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a bundle of products for discount.
 * Implements Value Object pattern - immutable and compared by value.
 *
 * @param name               -- GETTER --
 *                           Gets the bundle name.
 * @param requiredProducts   -- GETTER --
 *                           Gets required products and quantities.
 * @param discountPercentage -- GETTER --
 *                           Gets the discount percentage.
 */
public record ProductBundle(String name,
                            Map<Product, Integer> requiredProducts,
                            double discountPercentage) {

    /**
     * Default bundle discount percentage.
     */
    private static final double BUNDLE_DISCOUNT_PERCENTAGE = 10.0;

    /**
     * Maximum discount percentage.
     */
    private static final double MAX_DISCOUNT_PERCENTAGE = 100.0;

    /**
     * Compact constructor with validation.
     */
    public ProductBundle {
        name = Objects.requireNonNull(name, "Bundle name cannot be null");
        requiredProducts = Collections.unmodifiableMap(
                new HashMap<>(Objects.requireNonNull(requiredProducts,
                        "Required products cannot be null")));

        if (requiredProducts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bundle must contain at least one product");
        }
        if (discountPercentage < 0
                || discountPercentage > MAX_DISCOUNT_PERCENTAGE) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100");
        }
    }

    /**
     * Creates a product bundle with default 10% discount.
     *
     * @param bundleName       the bundle name
     * @param products         map of products to quantities
     * @return new ProductBundle with default discount
     */
    public static ProductBundle withDefaultDiscount(
            final String bundleName,
            final Map<Product, Integer> products) {
        return new ProductBundle(bundleName, products,
                BUNDLE_DISCOUNT_PERCENTAGE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductBundle that)) {
            return false;
        }
        return Double.compare(that.discountPercentage,
                discountPercentage) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(requiredProducts, that.requiredProducts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requiredProducts, discountPercentage);
    }

    @Override
    public String toString() {
        return String.format(
                "ProductBundle{name='%s', discount=%.1f%%%%}",
                name, discountPercentage);
    }
}

