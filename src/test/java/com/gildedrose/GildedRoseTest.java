package com.gildedrose;

import dojo.supermarket.model.gildedrose.GildedRose;
import dojo.supermarket.model.gildedrose.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    @Test
    void normalItemDegrades() {
        Item[] items = new Item[] { new Item("Normal Item", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, app.getItems()[0].sellIn);
        assertEquals(19, app.getItems()[0].quality);
    }

    @Test
    void normalItemDegradesTwiceAsFastAfterSellDate() {
        Item[] items = new Item[] { new Item("Normal Item", 0, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, app.getItems()[0].sellIn);
        assertEquals(18, app.getItems()[0].quality);
    }

    @Test
    void qualityNeverNegative() {
        Item[] items = new Item[] { new Item("Normal Item", 5, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(0, app.getItems()[0].quality);
    }

    @Test
    void agedBrieIncreasesInQuality() {
        Item[] items = new Item[] { new Item("Aged Brie", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, app.getItems()[0].sellIn);
        assertEquals(21, app.getItems()[0].quality);
    }

    @Test
    void qualityNeverMoreThan50() {
        Item[] items = new Item[] { new Item("Aged Brie", 10, 50) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(50, app.getItems()[0].quality);
    }

    @Test
    void sulfurasNeverChanges() {
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 10, 80) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(10, app.getItems()[0].sellIn);
        assertEquals(80, app.getItems()[0].quality);
    }

    @Test
    void backstagePassIncreasesBy2WhenSellInIs10OrLess() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, app.getItems()[0].sellIn);
        assertEquals(22, app.getItems()[0].quality);
    }

    @Test
    void backstagePassIncreasesBy3WhenSellInIs5OrLess() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(4, app.getItems()[0].sellIn);
        assertEquals(23, app.getItems()[0].quality);
    }

    @Test
    void backstagePassDropsTo0AfterConcert() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, app.getItems()[0].sellIn);
        assertEquals(0, app.getItems()[0].quality);
    }

    @Test
    void conjuredItemDegradesTwiceAsFast() {
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, app.getItems()[0].sellIn);
        assertEquals(18, app.getItems()[0].quality);
    }

    @Test
    void conjuredItemDegradesFourTimesAsFastAfterSellDate() {
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 0, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, app.getItems()[0].sellIn);
        assertEquals(16, app.getItems()[0].quality);
    }

    @Test
    void multipleItemsUpdateCorrectly() {
        Item[] items = new Item[] {
            new Item("Normal Item", 10, 20),
            new Item("Aged Brie", 5, 10),
            new Item("Sulfuras, Hand of Ragnaros", 0, 80),
            new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            new Item("Conjured Mana Cake", 3, 6)
        };
        GildedRose app = new GildedRose(items);
        app.updateQuality();

        assertEquals(19, app.getItems()[0].quality);
        assertEquals(11, app.getItems()[1].quality);
        assertEquals(80, app.getItems()[2].quality);
        assertEquals(21, app.getItems()[3].quality);
        assertEquals(4, app.getItems()[4].quality);
    }

    @Test
    void testTextTestFixtureScenario() {
        Item[] items = new Item[] {
            new Item("+5 Dexterity Vest", 10, 20),
            new Item("Aged Brie", 2, 0),
            new Item("Elixir of the Mongoose", 5, 7),
            new Item("Sulfuras, Hand of Ragnaros", 0, 80),
            new Item("Sulfuras, Hand of Ragnaros", -1, 80),
            new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
            new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
            new Item("Conjured Mana Cake", 3, 6)
        };
        GildedRose app = new GildedRose(items);
        app.updateQuality();

        // +5 Dexterity Vest - normal item
        assertEquals(9, items[0].sellIn);
        assertEquals(19, items[0].quality);

        // Aged Brie
        assertEquals(1, items[1].sellIn);
        assertEquals(1, items[1].quality);

        // Elixir of the Mongoose - normal item
        assertEquals(4, items[2].sellIn);
        assertEquals(6, items[2].quality);

        // Sulfuras - never changes
        assertEquals(0, items[3].sellIn);
        assertEquals(80, items[3].quality);
        assertEquals(-1, items[4].sellIn);
        assertEquals(80, items[4].quality);

        // Backstage passes
        assertEquals(14, items[5].sellIn);
        assertEquals(21, items[5].quality);
        assertEquals(9, items[6].sellIn);
        assertEquals(50, items[6].quality);
        assertEquals(4, items[7].sellIn);
        assertEquals(50, items[7].quality);

        // Conjured
        assertEquals(2, items[8].sellIn);
        assertEquals(4, items[8].quality);
    }

    @Test
    void normalItemQualityStaysAt0WhenAlready0() {
        Item[] items = new Item[] { new Item("Normal Item", 10, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        app.updateQuality();
        app.updateQuality();
        assertEquals(0, app.getItems()[0].quality);
    }

    @Test
    void agedBrieIncreasesBy2AfterSellDate() {
        Item[] items = new Item[] { new Item("Aged Brie", 0, 10) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(-1, app.getItems()[0].sellIn);
        assertEquals(12, app.getItems()[0].quality);
    }

    @Test
    void backstagePassIncreasesBy1WhenMoreThan10Days() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(14, app.getItems()[0].sellIn);
        assertEquals(21, app.getItems()[0].quality);
    }

    @Test
    void emptyItemsArrayDoesNotCrash() {
        Item[] items = new Item[] {};
        GildedRose app = new GildedRose(items);
        app.updateQuality(); // Should not throw exception
    }

    @Test
    void multipleDaysSimulation() {
        Item[] items = new Item[] {
            new Item("Normal Item", 5, 10),
            new Item("Aged Brie", 5, 10)
        };
        GildedRose app = new GildedRose(items);

        for (int i = 0; i < 10; i++) {
            app.updateQuality();
        }

        // After 10 days: normal item degrades
        assertEquals(-5, items[0].sellIn);
        assertEquals(0, items[0].quality); // Can't go below 0

        // After 10 days: aged brie improves
        assertEquals(-5, items[1].sellIn);
        assertEquals(25, items[1].quality); // 10 + (5*1) + (5*2)
    }

    @Test
    void conjuredItemCannotHaveNegativeQuality() {
        Item[] items = new Item[] { new Item("Conjured Sword", 5, 1) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(0, app.getItems()[0].quality);
    }

    @Test
    void backstagePassCannotExceed50EvenCloseToEvent() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 3, 48) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(50, app.getItems()[0].quality);
    }
}
