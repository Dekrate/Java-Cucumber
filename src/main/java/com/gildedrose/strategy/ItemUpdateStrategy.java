package com.gildedrose.strategy;

import com.gildedrose.Item;

/**
 * Strategy interface for updating item quality and sellIn values.
 * Each item type should have its own implementation.
 */
public interface ItemUpdateStrategy {
	/**
	 * Updates the quality and sellIn values of an item according to specific rules.
	 * @param item the item to update
	 */
	void update(Item item);

	/**
	 * Checks if this strategy can handle the given item.
	 * @param item the item to check
	 * @return true if this strategy can handle the item
	 */
	boolean canHandle(Item item);
}

