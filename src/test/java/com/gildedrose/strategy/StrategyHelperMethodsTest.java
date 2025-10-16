package com.gildedrose.strategy;

import dojo.supermarket.model.gildedrose.Item;
import dojo.supermarket.model.strategy.NormalItemStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for protected helper methods in strategy classes
 */
class StrategyHelperMethodsTest {

    // Test class to access protected methods
    private static class TestableNormalItemStrategy extends NormalItemStrategy {
        @Override
        public void decreaseQuality(Item item, int amount) {
            super.decreaseQuality(item, amount);
        }

        @Override
        public void increaseQuality(Item item, int amount) {
            super.increaseQuality(item, amount);
        }

        @Override
        public void decreaseSellIn(Item item) {
            super.decreaseSellIn(item);
        }
    }

    @Test
    void decreaseQuality_DecreasesQualityByAmount() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 20);
        strategy.decreaseQuality(item, 5);
        assertEquals(15, item.quality);
    }

    @Test
    void decreaseQuality_NeverGoesNegative() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 3);
        strategy.decreaseQuality(item, 5);
        assertEquals(0, item.quality);
    }

    @Test
    void decreaseQuality_StaysAt0WhenAlready0() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 0);
        strategy.decreaseQuality(item, 5);
        assertEquals(0, item.quality);
    }

    @Test
    void increaseQuality_IncreasesQualityByAmount() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 20);
        strategy.increaseQuality(item, 5);
        assertEquals(25, item.quality);
    }

    @Test
    void increaseQuality_NeverExceeds50() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 48);
        strategy.increaseQuality(item, 5);
        assertEquals(50, item.quality);
    }

    @Test
    void increaseQuality_StaysAt50WhenAlready50() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 50);
        strategy.increaseQuality(item, 5);
        assertEquals(50, item.quality);
    }

    @Test
    void increaseQuality_WithZeroAmount() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 20);
        strategy.increaseQuality(item, 0);
        assertEquals(20, item.quality);
    }

    @Test
    void increaseQuality_WithLargeAmount() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 10);
        strategy.increaseQuality(item, 100);
        assertEquals(50, item.quality);
    }

    @Test
    void decreaseSellIn_DecreasesBy1() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 10, 20);
        strategy.decreaseSellIn(item);
        assertEquals(9, item.sellIn);
    }

    @Test
    void decreaseSellIn_CanGoNegative() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", 0, 20);
        strategy.decreaseSellIn(item);
        assertEquals(-1, item.sellIn);
    }

    @Test
    void decreaseSellIn_CanGoDeeplyNegative() {
        TestableNormalItemStrategy strategy = new TestableNormalItemStrategy();
        Item item = new Item("Test", -5, 20);
        strategy.decreaseSellIn(item);
        assertEquals(-6, item.sellIn);
    }
}
