package com.gildedrose.loyalty;

import dojo.supermarket.model.loyalty.LoyaltyProgram;
import dojo.supermarket.model.loyalty.LoyaltyTier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramTest {

    private LoyaltyProgram program;

    @BeforeEach
    void setUp() {
        program = new LoyaltyProgram("CUST001");
    }

    @Test
    void newCustomerStartsWithBronzeTier() {
        assertEquals(LoyaltyTier.BRONZE, program.getTier());
        assertEquals(0, program.getPoints());
        assertEquals(5.0, program.getTierDiscount());
    }

    @Test
    void customerUpgradesToSilverAt500Points() {
        program.addPoints(500);
        assertEquals(LoyaltyTier.SILVER, program.getTier());
        assertEquals(10.0, program.getTierDiscount());
    }

    @Test
    void customerUpgradesToGoldAt1000Points() {
        program.addPoints(1000);
        assertEquals(LoyaltyTier.GOLD, program.getTier());
        assertEquals(15.0, program.getTierDiscount());
    }

    @Test
    void customerCanRedeemPoints() {
        program.addPoints(100);
        assertTrue(program.redeemPoints(50));
        assertEquals(50, program.getPoints());
    }

    @Test
    void customerCannotRedeemMorePointsThanTheyHave() {
        program.addPoints(50);
        assertFalse(program.redeemPoints(100));
        assertEquals(50, program.getPoints());
    }

    @Test
    void tierDowngradesWhenPointsRedeemed() {
        program.addPoints(500);
        assertEquals(LoyaltyTier.SILVER, program.getTier());
        program.redeemPoints(100);
        assertEquals(LoyaltyTier.BRONZE, program.getTier());
    }
}

