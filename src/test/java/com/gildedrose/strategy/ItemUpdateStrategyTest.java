package com.gildedrose.strategy;

import com.gildedrose.Item;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemUpdateStrategyTest {

    @Test
    void normalItemStrategyHandlesNormalItems() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        assertTrue(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertTrue(strategy.canHandle(new Item("+5 Dexterity Vest", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Elixir of the Mongoose", 5, 7)));
    }

    @Test
    void normalItemStrategyDoesNotHandleSpecialItems() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        assertFalse(strategy.canHandle(new Item("Aged Brie", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Sulfuras, Hand of Ragnaros", 10, 80)));
        assertFalse(strategy.canHandle(new Item("Conjured Mana Cake", 10, 20)));
    }

    @Test
    void normalItemStrategyUpdatesQualityCorrectly() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        Item item = new Item("Normal Item", 10, 20);
        strategy.update(item);
        assertEquals(9, item.sellIn);
        assertEquals(19, item.quality);
    }

    @Test
    void normalItemStrategyQualityNeverNegative() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        Item item = new Item("Normal Item", 10, 0);
        strategy.update(item);
        assertEquals(0, item.quality);
    }

    @Test
    void agedBrieStrategyHandlesAgedBrie() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        assertTrue(strategy.canHandle(new Item("Aged Brie", 10, 20)));
    }

    @Test
    void agedBrieStrategyDoesNotHandleOtherItems() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        assertFalse(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20)));
    }

    @Test
    void agedBrieStrategyIncreasesQuality() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", 10, 20);
        strategy.update(item);
        assertEquals(9, item.sellIn);
        assertEquals(21, item.quality);
    }

    @Test
    void agedBrieStrategyIncreasesQualityTwiceAfterSellDate() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", 0, 20);
        strategy.update(item);
        assertEquals(-1, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    void agedBrieStrategyQualityNeverExceeds50() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", 5, 50);
        strategy.update(item);
        assertEquals(50, item.quality);
    }

    @Test
    void agedBrieStrategyQualityNeverExceeds50AfterSellDate() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", -1, 49);
        strategy.update(item);
        assertEquals(50, item.quality);
    }

    @Test
    void sulfurasStrategyHandlesSulfuras() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        assertTrue(strategy.canHandle(new Item("Sulfuras, Hand of Ragnaros", 10, 80)));
    }

    @Test
    void sulfurasStrategyDoesNotHandleOtherItems() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        assertFalse(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Aged Brie", 10, 20)));
    }

    @Test
    void sulfurasStrategyNeverChanges() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        Item item = new Item("Sulfuras, Hand of Ragnaros", 10, 80);
        strategy.update(item);
        assertEquals(10, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    void sulfurasStrategyNeverChangesWithNegativeSellIn() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        Item item = new Item("Sulfuras, Hand of Ragnaros", -1, 80);
        strategy.update(item);
        assertEquals(-1, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    void backstagePassStrategyHandlesBackstagePasses() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        assertTrue(strategy.canHandle(new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20)));
    }

    @Test
    void backstagePassStrategyDoesNotHandleOtherItems() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        assertFalse(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Aged Brie", 10, 20)));
    }

    @Test
    void backstagePassStrategyIncreasesBy1WhenMoreThan10Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20);
        strategy.update(item);
        assertEquals(14, item.sellIn);
        assertEquals(21, item.quality);
    }

    @Test
    void backstagePassStrategyIncreasesBy2When10Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20);
        strategy.update(item);
        assertEquals(9, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    void backstagePassStrategyIncreasesBy2When6Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 6, 20);
        strategy.update(item);
        assertEquals(5, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    void backstagePassStrategyIncreasesBy3When5Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20);
        strategy.update(item);
        assertEquals(4, item.sellIn);
        assertEquals(23, item.quality);
    }

    @Test
    void backstagePassStrategyIncreasesBy3When1Day() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 1, 20);
        strategy.update(item);
        assertEquals(0, item.sellIn);
        assertEquals(23, item.quality);
    }

    @Test
    void backstagePassStrategyDropsTo0AfterConcert() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20);
        strategy.update(item);
        assertEquals(-1, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    void backstagePassStrategyQualityNeverExceeds50() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49);
        strategy.update(item);
        assertEquals(50, item.quality);
    }

    @Test
    void backstagePassStrategyQualityNeverExceeds50When10Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49);
        strategy.update(item);
        assertEquals(50, item.quality);
    }

    @Test
    void conjuredItemStrategyHandlesConjuredItems() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        assertTrue(strategy.canHandle(new Item("Conjured Mana Cake", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Conjured Elixir", 10, 20)));
    }

    @Test
    void conjuredItemStrategyDoesNotHandleOtherItems() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        assertFalse(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Aged Brie", 10, 20)));
    }

    @Test
    void conjuredItemStrategyDegradesTwiceAsFast() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Mana Cake", 10, 20);
        strategy.update(item);
        assertEquals(9, item.sellIn);
        assertEquals(18, item.quality);
    }

    @Test
    void conjuredItemStrategyDegradesFourTimesAsFastAfterSellDate() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Mana Cake", 0, 20);
        strategy.update(item);
        assertEquals(-1, item.sellIn);
        assertEquals(16, item.quality);
    }

    @Test
    void conjuredItemStrategyQualityNeverNegative() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Mana Cake", 10, 1);
        strategy.update(item);
        assertEquals(0, item.quality);
    }

    @Test
    void conjuredItemStrategyQualityNeverNegativeAfterSellDate() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Mana Cake", 0, 3);
        strategy.update(item);
        assertEquals(0, item.quality);
    }
}

