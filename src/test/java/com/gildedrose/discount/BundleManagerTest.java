package com.gildedrose.discount;

import com.gildedrose.Item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class BundleManagerTest {

    private BundleManager manager;

    @BeforeEach
    void setUp() {
        manager = new BundleManager();
    }

    @Test
    void canAddBundle() {
        DiscountBundle bundle = new DiscountBundle("Starter Pack", 10.0);
        manager.addBundle(bundle);
        assertEquals(1, manager.getAllBundles().size());
    }

    @Test
    void canRemoveBundle() {
        DiscountBundle bundle = new DiscountBundle("Starter Pack", 10.0);
        manager.addBundle(bundle);
        manager.removeBundle("Starter Pack");
        assertEquals(0, manager.getAllBundles().size());
    }

    @Test
    void canGetBundleByName() {
        DiscountBundle bundle = new DiscountBundle("Starter Pack", 10.0);
        manager.addBundle(bundle);
        assertTrue(manager.getBundle("Starter Pack").isPresent());
    }

    @Test
    void returnsEmptyForNonExistentBundle() {
        assertFalse(manager.getBundle("Non-existent").isPresent());
    }

    @Test
    void calculatesBestDiscount() {
        manager.addBundle(new DiscountBundle("Bundle A", 10.0));
        manager.addBundle(new DiscountBundle("Bundle B", 20.0));
        manager.addBundle(new DiscountBundle("Bundle C", 15.0));

        double bestDiscount = manager.calculateBestDiscount(100.0);
        assertEquals(20.0, bestDiscount, 0.01);
    }
}

