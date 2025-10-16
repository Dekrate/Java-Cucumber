package dojo.supermarket.model.loyalty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramTest {

    @Test
    @DisplayName("BasicLoyaltyTier should have correct properties")
    void testBasicTier() {
        LoyaltyProgram basic = new BasicLoyaltyTier();

        assertEquals("Basic", basic.getTierName());
        assertEquals(0.0, basic.getDiscountPercentage());
        assertEquals(1.0, basic.getPointsMultiplier());
        assertTrue(basic.isApplicable(5.00));
        assertTrue(basic.isApplicable(100.00));
    }

    @Test
    @DisplayName("SilverLoyaltyTier should have correct properties")
    void testSilverTier() {
        LoyaltyProgram silver = new SilverLoyaltyTier();

        assertEquals("Silver", silver.getTierName());
        assertEquals(5.0, silver.getDiscountPercentage());
        assertEquals(1.5, silver.getPointsMultiplier());
        assertFalse(silver.isApplicable(19.99));
        assertTrue(silver.isApplicable(20.00));
        assertTrue(silver.isApplicable(100.00));
    }

    @Test
    @DisplayName("GoldLoyaltyTier should have correct properties")
    void testGoldTier() {
        LoyaltyProgram gold = new GoldLoyaltyTier();

        assertEquals("Gold", gold.getTierName());
        assertEquals(10.0, gold.getDiscountPercentage());
        assertEquals(2.0, gold.getPointsMultiplier());
        assertFalse(gold.isApplicable(49.99));
        assertTrue(gold.isApplicable(50.00));
        assertTrue(gold.isApplicable(200.00));
    }
}

