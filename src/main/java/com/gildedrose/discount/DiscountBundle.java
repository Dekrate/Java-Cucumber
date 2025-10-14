package com.gildedrose.discount;

import com.gildedrose.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bundle of items sold together at a discounted price.
 */
public class DiscountBundle {
	private String bundleName;
	private List<Item> items;
	private double discountPercentage;

	public DiscountBundle(String bundleName, double discountPercentage) {
		this.bundleName = bundleName;
		this.discountPercentage = discountPercentage;
		this.items = new ArrayList<>();
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public List<Item> getItems() {
		return new ArrayList<>(items);
	}

	public String getBundleName() {
		return bundleName;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	/**
	 * Calculates the total quality value of all items in the bundle.
	 */
	public int getTotalQuality() {
		return items.stream().mapToInt(item -> item.quality).sum();
	}

	/**
	 * Calculates the discounted price based on total quality.
	 */
	public double getDiscountedPrice(double basePrice) {
		return basePrice * (1 - discountPercentage / 100.0);
	}

	@Override
	public String toString() {
		return String.format("Bundle: %s (%.0f%% off) - %d items",
				bundleName, discountPercentage, items.size());
	}
}

