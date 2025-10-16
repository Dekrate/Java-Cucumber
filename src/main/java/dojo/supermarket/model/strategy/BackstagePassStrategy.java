package dojo.supermarket.model.strategy;

import dojo.supermarket.model.gildedrose.Item;

/**
 * Strategy for Backstage passes which increase in quality as the concert approaches.
 * Quality increases by 1 normally, by 2 when 10 days or less, by 3 when 5 days or less.
 * Quality drops to 0 after the concert.
 */
public class BackstagePassStrategy implements ItemUpdateStrategy {

    @Override
    public void update(Item item) {
        if (item.sellIn <= 0) {
            item.quality = 0;
            item.sellIn--;
            return;
        }

        if (item.sellIn <= 5) {
            increaseQuality(item, 3);
        } else if (item.sellIn <= 10) {
            increaseQuality(item, 2);
        } else {
            increaseQuality(item, 1);
        }

        item.sellIn--;
    }

    @Override
    public boolean canHandle(Item item) {
        return item.name.equals("Backstage passes to a TAFKAL80ETC concert");
    }

    private void increaseQuality(Item item, int amount) {
        item.quality = Math.min(50, item.quality + amount);
    }
}
