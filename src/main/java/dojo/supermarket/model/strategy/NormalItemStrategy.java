package dojo.supermarket.model.strategy;

import dojo.supermarket.model.gildedrose.Item;

/**
 * Strategy for normal items that degrade in quality over time.
 * Quality degrades by 1 per day before sell date, and by 2 per day after.
 */
public class NormalItemStrategy implements ItemUpdateStrategy {

    @Override
    public void update(Item item) {
        decreaseQuality(item, 1);
        decreaseSellIn(item);

        if (item.sellIn < 0) {
            decreaseQuality(item, 1);
        }
    }

    @Override
    public boolean canHandle(Item item) {
        String name = item.name.toLowerCase();
        return !name.contains("aged brie")
            && !name.contains("backstage pass")
            && !name.contains("sulfuras")
            && !name.contains("conjured");
    }

    protected void decreaseQuality(Item item, int amount) {
        item.quality = Math.max(0, item.quality - amount);
    }

    protected void increaseQuality(Item item, int amount) {
        item.quality = Math.min(50, item.quality + amount);
    }

    protected void decreaseSellIn(Item item) {
        item.sellIn--;
    }
}
