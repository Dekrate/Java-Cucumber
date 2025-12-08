package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a receipt with items and discounts.
 */
public final class Receipt {

    /**
     * List of receipt items.
     */
    private final List<ReceiptItem> items = new ArrayList<>();

    /**
     * List of discounts applied.
     */
    private final List<Discount> discounts = new ArrayList<>();

    /**
     * Gets the total price including discounts.
     *
     * @return total price
     */
    public double getTotalPrice() {
        double total = 0.0;
        for (ReceiptItem item : items) {
            total += item.totalPrice();
        }
        for (Discount discount : discounts) {
            total += discount.discountAmount();
        }
        return total;
    }

    /**
     * Adds a product to the receipt.
     *
     * @param product    the product
     * @param quantity   the quantity
     * @param price      the unit price
     * @param totalPrice the total price for this product
     */
    public void addProduct(final Product product, final double quantity,
                           final double price, final double totalPrice) {
        items.add(new ReceiptItem(product, quantity, price, totalPrice));
    }

    /**
     * Gets all receipt items.
     *
     * @return unmodifiable list of receipt items
     */
    public List<ReceiptItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Adds a discount to the receipt.
     *
     * @param discount the discount to add
     */
    public void addDiscount(final Discount discount) {
        discounts.add(discount);
    }

    /**
     * Gets all discounts.
     *
     * @return unmodifiable list of discounts
     */
    public List<Discount> getDiscounts() {
        return Collections.unmodifiableList(discounts);
    }
}
