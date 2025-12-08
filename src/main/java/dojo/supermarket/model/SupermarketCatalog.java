package dojo.supermarket.model;

import java.math.BigDecimal;

/**
 * Catalog interface for product prices.
 */
public interface SupermarketCatalog {

    /**
     * Adds a product with its price to the catalog.
     *
     * @param product the product
     * @param price   the price
     */
    void addProduct(Product product, BigDecimal price);

    /**
     * Gets the unit price for a product.
     *
     * @param product the product
     * @return unit price
     */
    BigDecimal getUnitPrice(Product product);
}
