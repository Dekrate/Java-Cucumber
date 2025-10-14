package com.gildedrose;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to verify all original requirements are met
 */
class GildedRoseRequirementsTest {

    @Test
    void requirement_AllItemsHaveSellInValue() {
        Item item = new Item("Test", 10, 20);
        assertNotNull(item.sellIn);
    }

    @Test
    void requirement_AllItemsHaveQualityValue() {
        Item item = new Item("Test", 10, 20);
        assertNotNull(item.quality);
    }

    @Test
    void requirement_SystemLowersBothValuesForEveryItem() {
        Item[] items = new Item[] { new Item("Normal Item", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(9, items[0].sellIn);
        assertEquals(19, items[0].quality);
    }

    @Test
    void requirement_QualityDegradesTwiceAsFastAfterSellDate() {
        Item[] items = new Item[] { new Item("Normal Item", 0, 10) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(8, items[0].quality); // Degraded by 2
    }

    @Test
    void requirement_QualityNeverNegative() {
        Item[] items = new Item[] { new Item("Normal Item", 5, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertTrue(items[0].quality >= 0);
    }

    @Test
    void requirement_AgedBrieIncreasesInQuality() {
        Item[] items = new Item[] { new Item("Aged Brie", 10, 10) };
        GildedRose app = new GildedRose(items);
        int initialQuality = items[0].quality;
        app.updateQuality();
        assertTrue(items[0].quality > initialQuality);
    }

    @Test
    void requirement_QualityNeverMoreThan50() {
        Item[] items = new Item[] { new Item("Aged Brie", 10, 50) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertTrue(items[0].quality <= 50);
    }

    @Test
    void requirement_SulfurasNeverHasToBeSold() {
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 10, 80) };
        GildedRose app = new GildedRose(items);
        int initialSellIn = items[0].sellIn;
        app.updateQuality();
        assertEquals(initialSellIn, items[0].sellIn);
    }

    @Test
    void requirement_SulfurasNeverDecreasesInQuality() {
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 10, 80) };
        GildedRose app = new GildedRose(items);
        int initialQuality = items[0].quality;
        app.updateQuality();
        assertEquals(initialQuality, items[0].quality);
    }

    @Test
    void requirement_BackstagePassIncreasesInQuality() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        int initialQuality = items[0].quality;
        app.updateQuality();
        assertTrue(items[0].quality > initialQuality);
    }

    @Test
    void requirement_BackstagePassIncreasesBy2When10DaysOrLess() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(22, items[0].quality); // +2
    }

    @Test
    void requirement_BackstagePassIncreasesBy3When5DaysOrLess() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(23, items[0].quality); // +3
    }

    @Test
    void requirement_BackstagePassDropsTo0AfterConcert() {
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals(0, items[0].quality);
    }

    @Test
    void requirement_ConjuredItemsDegradeTwiceAsFast() {
        Item[] normalItems = new Item[] { new Item("Normal Item", 10, 20) };
        Item[] conjuredItems = new Item[] { new Item("Conjured Mana Cake", 10, 20) };

        GildedRose normalApp = new GildedRose(normalItems);
        GildedRose conjuredApp = new GildedRose(conjuredItems);

        normalApp.updateQuality();
        conjuredApp.updateQuality();

        int normalDegradation = 20 - normalItems[0].quality; // Should be 1
        int conjuredDegradation = 20 - conjuredItems[0].quality; // Should be 2

        assertEquals(2 * normalDegradation, conjuredDegradation);
    }

    @Test
    void requirement_SulfurasQualityIs80() {
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 0, 80) };
        GildedRose app = new GildedRose(items);
        assertEquals(80, items[0].quality);
    }

    @Test
    void requirement_ItemClassUnchanged() {
        // Verify Item class still has public fields (as required - no encapsulation)
        Item item = new Item("Test", 10, 20);
        item.sellIn = 5;
        item.quality = 15;
        assertEquals(5, item.sellIn);
        assertEquals(15, item.quality);
    }

    @Test
    void requirement_ItemsPropertyAccessible() {
        // Verify items property is accessible (as required)
        Item[] items = new Item[] { new Item("Test", 10, 20) };
        GildedRose app = new GildedRose(items);
        assertNotNull(app.items);
        assertEquals(1, app.items.length);
    }

    @Test
    void longTermSimulation_30Days() {
        Item[] items = new Item[] {
            new Item("+5 Dexterity Vest", 10, 20),
            new Item("Aged Brie", 2, 0),
            new Item("Sulfuras, Hand of Ragnaros", 0, 80),
            new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            new Item("Conjured Mana Cake", 3, 6)
        };
        GildedRose app = new GildedRose(items);

        for (int day = 0; day < 30; day++) {
            app.updateQuality();

            // Verify constraints hold every day
            for (Item item : items) {
                assertTrue(item.quality >= 0, "Quality should never be negative on day " + day);
                if (!item.name.equals("Sulfuras, Hand of Ragnaros")) {
                    assertTrue(item.quality <= 50, "Quality should never exceed 50 on day " + day);
                }
            }
        }

        // Sulfuras should remain unchanged
        assertEquals(80, items[2].quality);
        assertEquals(0, items[2].sellIn);
    }
}

