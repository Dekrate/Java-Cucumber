package com.gildedrose.strategy;

import dojo.supermarket.model.gildedrose.Item;
import dojo.supermarket.model.strategy.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive edge case tests for all strategies
 */
class StrategyEdgeCasesTest {

    @Test
    void normalItemStrategy_WithNegativeSellIn() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        Item item = new Item("Test", -1, 10);
        strategy.update(item);
        assertEquals(-2, item.sellIn);
        assertEquals(8, item.quality); // -2 because already past sell date
    }

    @Test
    void normalItemStrategy_WithVeryLowQuality() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        Item item = new Item("Test", -1, 1);
        strategy.update(item);
        assertEquals(0, item.quality);
    }

    @Test
    void agedBrieStrategy_AtQuality49() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", 10, 49);
        strategy.update(item);
        assertEquals(50, item.quality);
    }

    @Test
    void agedBrieStrategy_AtQuality49AfterSellDate() {
        AgedBrieStrategy strategy = new AgedBrieStrategy();
        Item item = new Item("Aged Brie", -1, 49);
        strategy.update(item);
        assertEquals(50, item.quality); // Should cap at 50, not 51
    }

    @Test
    void backstagePassStrategy_At11Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 11, 20);
        strategy.update(item);
        assertEquals(10, item.sellIn);
        assertEquals(21, item.quality); // Only +1
    }

    @Test
    void backstagePassStrategy_At6Days() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 6, 20);
        strategy.update(item);
        assertEquals(5, item.sellIn);
        assertEquals(22, item.quality); // +2
    }

    @Test
    void backstagePassStrategy_At1Day() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 1, 20);
        strategy.update(item);
        assertEquals(0, item.sellIn);
        assertEquals(23, item.quality); // +3
    }

    @Test
    void backstagePassStrategy_AtNegativeSellIn() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", -1, 20);
        strategy.update(item);
        assertEquals(-2, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    void backstagePassStrategy_At5DaysQuality48() {
        BackstagePassStrategy strategy = new BackstagePassStrategy();
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 48);
        strategy.update(item);
        assertEquals(50, item.quality); // Should cap at 50
    }

    @Test
    void conjuredItemStrategy_WithVeryLowQuality() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Item", 5, 1);
        strategy.update(item);
        assertEquals(0, item.quality);
    }

    @Test
    void conjuredItemStrategy_AfterSellDateWith3Quality() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        Item item = new Item("Conjured Item", 0, 3);
        strategy.update(item);
        assertEquals(0, item.quality); // -4 but capped at 0
    }

    @Test
    void sulfurasStrategy_WithDifferentQuality() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        Item item = new Item("Sulfuras, Hand of Ragnaros", 5, 80);
        strategy.update(item);
        assertEquals(5, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    void sulfurasStrategy_WithZeroSellIn() {
        SulfurasStrategy strategy = new SulfurasStrategy();
        Item item = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        strategy.update(item);
        assertEquals(0, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    void conjuredItemStrategy_WithVariousNames() {
        ConjuredItemStrategy strategy = new ConjuredItemStrategy();
        assertTrue(strategy.canHandle(new Item("Conjured Mana Cake", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Conjured Sword", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Conjured Health Potion", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Not Conjured", 10, 20)));
    }

    @Test
    void normalItemStrategy_WithVariousNames() {
        NormalItemStrategy strategy = new NormalItemStrategy();
        assertTrue(strategy.canHandle(new Item("+5 Dexterity Vest", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Elixir of the Mongoose", 10, 20)));
        assertTrue(strategy.canHandle(new Item("Normal Item", 10, 20)));
        assertFalse(strategy.canHandle(new Item("Aged Brie", 10, 20)));
    }
}
