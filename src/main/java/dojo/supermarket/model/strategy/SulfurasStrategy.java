package dojo.supermarket.model.strategy;

import dojo.supermarket.model.gildedrose.Item;

/**
 * Strategy for Sulfuras (legendary item).
 * Never decreases in quality or needs to be sold.
 */
public class SulfurasStrategy implements ItemUpdateStrategy {

    @Override
    public void update(Item item) {
        // Sulfuras never changes
    }

    @Override
    public boolean canHandle(Item item) {
        return item.name.equals("Sulfuras, Hand of Ragnaros");
    }
}
