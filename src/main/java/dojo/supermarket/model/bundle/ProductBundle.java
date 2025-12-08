package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a bundle of products for discount.
 * Implements Value Object pattern - immutable and compared by value.
 *
 * @param name the bundle name
 * @param requiredProducts map of products and their required quantities
 * @param discountPercentage the discount percentage for the bundle
 */
public record ProductBundle(String name,
                            Map<Product, Integer> requiredProducts,
                            BigDecimal discountPercentage) {

    /** Default discount percentage for bundles. */
    private static final BigDecimal BUNDLE_DISCOUNT_PERCENTAGE =
            new BigDecimal("10.0");
    /** Maximum allowed discount percentage. */
    private static final BigDecimal MAX_DISCOUNT_PERCENTAGE =
            new BigDecimal("100.0");

    /**
     * Compact constructor with validation.
     */
    public ProductBundle {
        Objects.requireNonNull(name, "Bundle name cannot be null");
        Objects.requireNonNull(requiredProducts,
                "Required products cannot be null");
        Objects.requireNonNull(discountPercentage,
                "Discount percentage cannot be null");

        if (requiredProducts.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bundle must contain at least one product");
        }
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0
                || discountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
            throw new IllegalArgumentException(
                    "Discount percentage must be between 0 and 100");
        }

        requiredProducts = Map.copyOf(requiredProducts);
    }

    /**
     * Creates a product bundle with default 10% discount.
     *
     * @param bundleName the bundle name
     * @param products   map of products to quantities
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
        if (!(o instanceof ProductBundle(
                String name1,
                Map<Product, Integer> products,
                BigDecimal percentage))) {
            return false;
        }
        return discountPercentage.compareTo(percentage) == 0
                && Objects.equals(name, name1)
                && Objects.equals(requiredProducts, products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, requiredProducts, discountPercentage);
    }

    @Override
    public String toString() {
        return "ProductBundle{"
                + "name='" + name + '\''
                + ", requiredProducts=" + requiredProducts
                + ", discountPercentage=" + discountPercentage
                + '}';
    }
}

