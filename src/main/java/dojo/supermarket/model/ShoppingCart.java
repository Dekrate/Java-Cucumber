package dojo.supermarket.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a shopping cart with products.
 */
public final class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, BigDecimal> productQuantities =
            new HashMap<>();

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
        addItemQuantity(product, BigDecimal.ONE);
    }

    /**
     * Gets product quantities map.
     *
     * @return unmodifiable map of product quantities
     */
    public Map<Product, BigDecimal> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    /**
     * Adds a product with specified quantity.
     *
     * @param product  the product to add
     * @param quantity the quantity
     */
    public void addItemQuantity(final Product product,
                                 final BigDecimal quantity) {
        items.add(new ProductQuantity(product, quantity));
        productQuantities.merge(product, quantity, BigDecimal::add);
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
        for (Map.Entry<Product, BigDecimal> entry
                : productQuantities.entrySet()) {
            final Product product = entry.getKey();
            final BigDecimal quantity = entry.getValue();

            if (offers.containsKey(product)) {
                final Offer offer = offers.get(product);
                final BigDecimal unitPrice = catalog.getUnitPrice(product);

                final Discount discount =
                        offer.calculateDiscount(quantity, unitPrice);
                if (discount != null) {
                    receipt.addDiscount(discount);
                }
            }
        }
    }
}

