package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a shopping cart with products.
 */
public final class ShoppingCart {

    /**
     * List of product quantities in the cart.
     */
    private final List<ProductQuantity> items = new ArrayList<>();

    /**
     * Map of products to their total quantities.
     */
    private final Map<Product, Double> productQuantities = new HashMap<>();

    /**
     * Gets all items in the cart.
     *
     * @return unmodifiable list of items
     */
    public List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds a single item to the cart.
     *
     * @param product the product to add
     */
    public void addItem(final Product product) {
        addItemQuantity(product, 1.0);
    }

    /**
     * Gets product quantities map.
     *
     * @return unmodifiable map of product quantities
     */
    public Map<Product, Double> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    /**
     * Adds a product with specified quantity.
     *
     * @param product  the product to add
     * @param quantity the quantity
     */
    public void addItemQuantity(final Product product,
                                 final double quantity) {
        items.add(new ProductQuantity(product, quantity));
        productQuantities.merge(product, quantity, Double::sum);
    }

    /**
     * Handles offer application for products in cart.
     *
     * @param receipt the receipt to add discounts to
     * @param offers  available offers
     * @param catalog the catalog for price lookup
     */
    void handleOffers(final Receipt receipt,
                      final Map<Product, Offer> offers,
                      final SupermarketCatalog catalog) {
        for (Map.Entry<Product, Double> entry
                : productQuantities.entrySet()) {
            final Product product = entry.getKey();
            final double quantity = entry.getValue();

            if (offers.containsKey(product)) {
                final Offer offer = offers.get(product);
                final double unitPrice = catalog.getUnitPrice(product);

                final Discount discount =
                        offer.calculateDiscount(quantity, unitPrice);
                if (discount != null) {
                    receipt.addDiscount(discount);
                }
            }
        }
    }
}
