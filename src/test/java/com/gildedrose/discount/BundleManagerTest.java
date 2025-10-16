package com.gildedrose.discount;

import dojo.supermarket.model.gildedrose.discount.BundleManager;
import dojo.supermarket.model.gildedrose.discount.DiscountBundle;
import dojo.supermarket.model.gildedrose.discount.DiscountStrategy;
import dojo.supermarket.model.gildedrose.Item;

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

    // New tests for OCP compliance

    @Test
    void canAddCustomDiscountStrategy() {
        DiscountStrategy customStrategy = new DiscountStrategy() {
            @Override
            public double calculateDiscount(double basePrice) {
                return basePrice * 0.25;
            }

            @Override
            public String getDescription() {
                return "Custom 25% discount";
            }
        };

        manager.addDiscountStrategy(customStrategy);
        assertEquals(1, manager.getAllDiscountStrategies().size());
    }

    @Test
    void canRemoveDiscountStrategy() {
        DiscountStrategy strategy = new DiscountStrategy() {
            @Override
            public double calculateDiscount(double basePrice) {
                return basePrice * 0.15;
            }

            @Override
            public String getDescription() {
                return "15% discount";
            }
        };

        manager.addDiscountStrategy(strategy);
        manager.removeDiscountStrategy(strategy);
        assertEquals(0, manager.getAllDiscountStrategies().size());
    }

    @Test
    void getBestDiscountStrategyReturnsHighestDiscount() {
        manager.addBundle(new DiscountBundle("Bundle A", 10.0));
        manager.addBundle(new DiscountBundle("Bundle B", 25.0));
        manager.addBundle(new DiscountBundle("Bundle C", 15.0));

        var bestStrategy = manager.getBestDiscountStrategy(100.0);
        assertTrue(bestStrategy.isPresent());
        assertEquals(25.0, bestStrategy.get().calculateDiscount(100.0), 0.01);
    }

    @Test
    void getBestDiscountStrategyReturnsEmptyWhenNoStrategies() {
        var bestStrategy = manager.getBestDiscountStrategy(100.0);
        assertFalse(bestStrategy.isPresent());
    }

    @Test
    void getAllDiscountStrategiesIncludesBundlesAndCustomStrategies() {
        manager.addBundle(new DiscountBundle("Bundle", 10.0));

        DiscountStrategy customStrategy = new DiscountStrategy() {
            @Override
            public double calculateDiscount(double basePrice) {
                return basePrice * 0.20;
            }

            @Override
            public String getDescription() {
                return "Custom 20%";
            }
        };
        manager.addDiscountStrategy(customStrategy);

        assertEquals(2, manager.getAllDiscountStrategies().size());
    }

    @Test
    void calculateBestDiscountWithZeroPrice() {
        manager.addBundle(new DiscountBundle("Bundle A", 10.0));
        double bestDiscount = manager.calculateBestDiscount(0.0);
        assertEquals(0.0, bestDiscount, 0.01);
    }

    @Test
    void calculateBestDiscountWithNoStrategies() {
        double bestDiscount = manager.calculateBestDiscount(100.0);
        assertEquals(0.0, bestDiscount, 0.01);
    }
}
