package com.gildedrose.loyalty;

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
}

