package dojo.supermarket.model;

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
    void addProduct(Product product, double price);

    /**
     * Gets the unit price for a product.
     *
     * @param product the product
     * @return unit price
     */
    double getUnitPrice(Product product);
}
