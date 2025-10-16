package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;

/**
 * Strategy interface for calculating discounts on products.
 * Each implementation represents a different type of special offer.
 * This design follows the Open/Closed Principle - new offer types
 * can be added by creating new implementations without modifying existing code.
 */
public interface OfferStrategy {

	/**
	 * Calculates the discount for a given product and quantity.
	 *
	 * @param product the product being purchased
	 * @param quantity the quantity purchased
	 * @param unitPrice the unit price from catalog
	 * @param argument the offer-specific argument (percentage, amount, etc.)
	 * @return Discount object if applicable, null otherwise
	 */
	Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument);

	/**
	 * Gets the description pattern for this offer type.
	 */
	String getDescription();
}

