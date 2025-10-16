package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Product;

import java.util.List;
import java.util.Objects;

/**
 * Represents a discounted bundle of products.
 * Bundles allow purchasing multiple products together at a reduced price.
 *
 * This enables the Open/Closed Principle - new bundles can be added
 * without modifying existing discount calculation logic.
 */
public class ProductBundle {

	private final String name;
	private final List<Product> products;
	private final double discountPercentage;
	private final String description;

	public ProductBundle(String name, List<Product> products, double discountPercentage) {
		this.name = name;
		this.products = products;
		this.discountPercentage = discountPercentage;
		this.description = name + " bundle - " + discountPercentage + "% off";
	}

	public String getName() {
		return name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Checks if all products in the bundle are present in the cart.
	 */
	public boolean isApplicable(List<Product> cartProducts) {
		return cartProducts.containsAll(products);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProductBundle)) return false;
		ProductBundle that = (ProductBundle) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

