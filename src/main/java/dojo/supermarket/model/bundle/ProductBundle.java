package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a bundle of products for discount.
 * Implements Value Object pattern - immutable and compared by value.
 */
public record ProductBundle(String name,
                            Map<Product, Integer> requiredProducts,
                            BigDecimal discountPercentage) {

	private static final BigDecimal BUNDLE_DISCOUNT_PERCENTAGE =
			new BigDecimal("10.0");
	private static final BigDecimal MAX_DISCOUNT_PERCENTAGE =
			new BigDecimal("100.0");

    /**
     * Compact constructor with validation.
     */
    public ProductBundle {
        name = Objects.requireNonNull(name, "Bundle name cannot be null");
        requiredProducts = Collections.unmodifiableMap(
                new HashMap<>(Objects.requireNonNull(requiredProducts,
                        "Required products cannot be null")));
        discountPercentage = Objects.requireNonNull(discountPercentage,
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
        if (!(o instanceof ProductBundle that)) {
            return false;
        }
        return discountPercentage.compareTo(that.discountPercentage) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(requiredProducts, that.requiredProducts);
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

