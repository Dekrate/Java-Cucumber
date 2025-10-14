package com.gildedrose.discount;

import com.gildedrose.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class DiscountBundleTest {

    private DiscountBundle bundle;

    @BeforeEach
    void setUp() {
        bundle = new DiscountBundle("Adventurer's Pack", 15.0);
    }

    @Test
    void bundleCanAddItems() {
        bundle.addItem(new Item("Sword", 10, 20));
        bundle.addItem(new Item("Shield", 15, 25));
        assertEquals(2, bundle.getItems().size());
    }

    @Test
    void bundleCalculatesTotalQuality() {
        bundle.addItem(new Item("Sword", 10, 20));
        bundle.addItem(new Item("Shield", 15, 30));
        assertEquals(50, bundle.getTotalQuality());
    }

    @Test
    void bundleAppliesDiscount() {
        double discountedPrice = bundle.getDiscountedPrice(100.0);
        assertEquals(85.0, discountedPrice, 0.01);
    }

    @Test
    void bundleHasCorrectName() {
        assertEquals("Adventurer's Pack", bundle.getBundleName());
    }

    @Test
    void bundleHasCorrectDiscountPercentage() {
        assertEquals(15.0, bundle.getDiscountPercentage());
    }
}

