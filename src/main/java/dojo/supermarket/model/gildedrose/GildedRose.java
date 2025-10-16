package dojo.supermarket.model.gildedrose;

import dojo.supermarket.model.strategy.*;
import java.util.ArrayList;
import java.util.List;

public class GildedRose {
    private final Item[] items;
    private List<ItemUpdateStrategy> strategies;

    public GildedRose(Item[] items) {
        this.items = items;
        initializeStrategies();
    }

    private void initializeStrategies() {
        strategies = new ArrayList<>();
        strategies.add(new SulfurasStrategy());
        strategies.add(new AgedBrieStrategy());
        strategies.add(new BackstagePassStrategy());
        strategies.add(new ConjuredItemStrategy());
        strategies.add(new NormalItemStrategy());
    }

    public void updateQuality() {
        for (Item item : items) {
            for (ItemUpdateStrategy strategy : strategies) {
                if (strategy.canHandle(item)) {
                    strategy.update(item);
                    break;
                }
            }
        }
    }

	public Item[] getItems() {
		return items;
	}
}
