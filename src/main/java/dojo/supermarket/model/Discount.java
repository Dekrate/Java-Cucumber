package dojo.supermarket.model;

import java.math.BigDecimal;

/**
 * Represents a discount applied to a product.
 *
 * @param product        the product
 * @param description    the discount description
 * @param discountAmount the discount amount
 */
public record Discount(Product product, String description,
                       BigDecimal discountAmount) {
}
