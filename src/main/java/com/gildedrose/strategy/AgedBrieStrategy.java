package com.gildedrose.strategy;

import com.gildedrose.Item;

/**
 * Strategy for Aged Brie which increases in quality over time.
 */
public class AgedBrieStrategy implements ItemUpdateStrategy {

    @Override
    public void update(Item item) {
        increaseQuality(item, 1);
        item.sellIn--;

        if (item.sellIn < 0) {
            increaseQuality(item, 1);
        }
    }

    @Override
    public boolean canHandle(Item item) {
        return item.name.equals("Aged Brie");
    }

    private void increaseQuality(Item item, int amount) {
        item.quality = Math.min(50, item.quality + amount);
    }
}

