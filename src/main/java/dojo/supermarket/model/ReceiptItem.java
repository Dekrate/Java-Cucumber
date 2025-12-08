package dojo.supermarket.model;

/**
 * Represents an item on a receipt.
 *
 * @param product    the product
 * @param quantity   the quantity
 * @param price      the unit price
 * @param totalPrice the total price
 */
public record ReceiptItem(Product product, double quantity,
                          double price, double totalPrice) {
}
