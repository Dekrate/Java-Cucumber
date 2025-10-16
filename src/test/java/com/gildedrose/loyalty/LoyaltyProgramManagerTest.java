package com.gildedrose.loyalty;

import dojo.supermarket.model.loyalty.LoyaltyProgramManager;
import dojo.supermarket.model.loyalty.LoyaltyProgram;
import dojo.supermarket.model.loyalty.LoyaltyStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramManagerTest {

    private LoyaltyProgramManager manager;

    @BeforeEach
    void setUp() {
        manager = new LoyaltyProgramManager();
    }

    @Test
    void canRegisterNewCustomer() {
        LoyaltyProgram program = manager.registerCustomer("CUST001");
        assertNotNull(program);
        assertEquals("CUST001", program.getCustomerId());
    }

    @Test
    void registeringExistingCustomerReturnsSameProgram() {
        LoyaltyProgram program1 = manager.registerCustomer("CUST001");
        program1.addPoints(100);

        LoyaltyProgram program2 = manager.registerCustomer("CUST001");
        assertEquals(100, program2.getPoints());
    }

    @Test
    void canAwardPointsForPurchase() {
        manager.registerCustomer("CUST001");
        manager.awardPointsForPurchase("CUST001", 150.75);

        LoyaltyProgram program = manager.getCustomerProgram("CUST001").get();
        assertEquals(150, program.getPoints());
    }

    @Test
    void applyLoyaltyDiscountForBronzeTier() {
        manager.registerCustomer("CUST001");
        double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0);
        assertEquals(95.0, finalPrice, 0.01);
    }

    @Test
    void applyLoyaltyDiscountForSilverTier() {
        manager.registerCustomer("CUST001");
        manager.awardPointsForPurchase("CUST001", 500.0);
        double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0);
        assertEquals(90.0, finalPrice, 0.01);
    }

    @Test
    void applyLoyaltyDiscountForGoldTier() {
        manager.registerCustomer("CUST001");
        manager.awardPointsForPurchase("CUST001", 1000.0);
        double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0);
        assertEquals(85.0, finalPrice, 0.01);
    }

    @Test
    void nonRegisteredCustomerGetsNoDiscount() {
        double finalPrice = manager.applyLoyaltyDiscount("UNKNOWN", 100.0);
        assertEquals(100.0, finalPrice, 0.01);
    }

    // New tests for OCP compliance

    @Test
    void canRegisterCustomerWithCustomLoyaltyStrategy() {
        LoyaltyStrategy customStrategy = new LoyaltyStrategy() {
            @Override
            public int calculatePoints(double purchaseAmount) {
                return (int) (purchaseAmount * 2); // Double points
            }

            @Override
            public double calculateDiscount() {
                return 25.0; // 25% discount
            }

            @Override
            public String getDescription() {
                return "VIP Program: 2x points, 25% discount";
            }
        };

        manager.registerCustomerWithStrategy("VIP001", customStrategy);
        assertTrue(manager.getCustomerStrategy("VIP001").isPresent());
    }

    @Test
    void customLoyaltyStrategyAppliesCorrectDiscount() {
        LoyaltyStrategy vipStrategy = new LoyaltyStrategy() {
            @Override
            public int calculatePoints(double purchaseAmount) {
                return (int) purchaseAmount;
            }

            @Override
            public double calculateDiscount() {
                return 30.0;
            }

            @Override
            public String getDescription() {
                return "VIP 30% off";
            }
        };

        manager.registerCustomerWithStrategy("VIP001", vipStrategy);
        double finalPrice = manager.applyLoyaltyDiscount("VIP001", 100.0);
        assertEquals(70.0, finalPrice, 0.01);
    }

    @Test
    void getCustomerStrategyReturnsCorrectStrategy() {
        manager.registerCustomer("CUST001");
        var strategy = manager.getCustomerStrategy("CUST001");
        assertTrue(strategy.isPresent());
        assertTrue(strategy.get() instanceof LoyaltyProgram);
    }

    @Test
    void getCustomerStrategyReturnsEmptyForUnknownCustomer() {
        var strategy = manager.getCustomerStrategy("UNKNOWN");
        assertFalse(strategy.isPresent());
    }

    @Test
    void getAllCustomerStrategiesReturnsAllRegistered() {
        manager.registerCustomer("CUST001");
        manager.registerCustomer("CUST002");

        LoyaltyStrategy vipStrategy = new LoyaltyStrategy() {
            @Override
            public int calculatePoints(double purchaseAmount) {
                return (int) purchaseAmount;
            }

            @Override
            public double calculateDiscount() {
                return 25.0;
            }

            @Override
            public String getDescription() {
                return "VIP";
            }
        };
        manager.registerCustomerWithStrategy("VIP001", vipStrategy);

        assertEquals(3, manager.getAllCustomerStrategies().size());
    }

    @Test
    void getAllCustomerProgramsReturnsOnlyLoyaltyPrograms() {
        manager.registerCustomer("CUST001");
        manager.registerCustomer("CUST002");

        LoyaltyStrategy vipStrategy = new LoyaltyStrategy() {
            @Override
            public int calculatePoints(double purchaseAmount) {
                return (int) purchaseAmount;
            }

            @Override
            public double calculateDiscount() {
                return 25.0;
            }

            @Override
            public String getDescription() {
                return "VIP";
            }
        };
        manager.registerCustomerWithStrategy("VIP001", vipStrategy);

        // Only 2 LoyaltyProgram instances, not the custom strategy
        assertEquals(2, manager.getAllCustomerPrograms().size());
    }

    @Test
    void loyaltyStrategyInterfaceMethodsWorkCorrectly() {
        LoyaltyProgram program = manager.registerCustomer("CUST001");

        assertEquals(5.0, program.calculateDiscount(), 0.01);
        assertEquals(100, program.calculatePoints(100.50));
        assertNotNull(program.getDescription());
    }
}
