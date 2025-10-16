package dojo.supermarket.model.strategy;

import dojo.supermarket.model.gildedrose.Item;

/**
 * Strategy for Conjured items that degrade twice as fast as normal items.
 */
public class ConjuredItemStrategy implements ItemUpdateStrategy {

    @Override
    public void update(Item item) {
        decreaseQuality(item, 2);
        item.sellIn--;

        if (item.sellIn < 0) {
            decreaseQuality(item, 2);
        }
    }

    @Override
    public boolean canHandle(Item item) {
        return item.name.startsWith("Conjured");
    }

    private void decreaseQuality(Item item, int amount) {
        item.quality = Math.max(0, item.quality - amount);
    }
}
