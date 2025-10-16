package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyProgramManagerTest {

    private LoyaltyProgramManager manager;

    @BeforeEach
    void setUp() {
        manager = new LoyaltyProgramManager();
    }

    @Test
    @DisplayName("Manager should return Basic tier for low amounts")
    void testBasicTierForLowAmount() {
        LoyaltyProgram tier = manager.getApplicableTier(10.00);

        assertEquals("Basic", tier.getTierName());
    }

    @Test
    @DisplayName("Manager should return Silver tier for medium amounts")
    void testSilverTierForMediumAmount() {
        LoyaltyProgram tier = manager.getApplicableTier(25.00);

        assertEquals("Silver", tier.getTierName());
    }

    @Test
    @DisplayName("Manager should return Gold tier for high amounts")
    void testGoldTierForHighAmount() {
        LoyaltyProgram tier = manager.getApplicableTier(60.00);

        assertEquals("Gold", tier.getTierName());
    }

    @Test
    @DisplayName("Manager should calculate discount for Silver tier")
    void testCalculateDiscountForSilver() {
        Product product = new Product("test", ProductUnit.EACH);

        Discount discount = manager.calculateLoyaltyDiscount(25.00, product);

        assertNotNull(discount);
        assertEquals(-1.25, discount.getDiscountAmount(), 0.01);
        assertTrue(discount.getDescription().contains("Silver Member"));
        assertTrue(discount.getDescription().contains("5.0% off"));
    }

    @Test
    @DisplayName("Manager should calculate discount for Gold tier")
    void testCalculateDiscountForGold() {
        Product product = new Product("test", ProductUnit.EACH);

        Discount discount = manager.calculateLoyaltyDiscount(60.00, product);

        assertNotNull(discount);
        assertEquals(-6.00, discount.getDiscountAmount(), 0.01);
        assertTrue(discount.getDescription().contains("Gold Member"));
        assertTrue(discount.getDescription().contains("10.0% off"));
    }

    @Test
    @DisplayName("Manager should return null discount for Basic tier")
    void testNoDiscountForBasic() {
        Product product = new Product("test", ProductUnit.EACH);

        Discount discount = manager.calculateLoyaltyDiscount(10.00, product);

        assertNull(discount);
    }

    @Test
    @DisplayName("Manager should allow adding new loyalty programs")
    void testAddLoyaltyProgram() {
        LoyaltyProgram customTier = new GoldLoyaltyTier();

        assertDoesNotThrow(() -> manager.addLoyaltyProgram(customTier));
    }

    @Test
    @DisplayName("Manager should return Basic tier at boundary")
    void testBoundaryConditions() {
        assertEquals("Basic", manager.getApplicableTier(19.99).getTierName());
        assertEquals("Silver", manager.getApplicableTier(20.00).getTierName());
        assertEquals("Silver", manager.getApplicableTier(49.99).getTierName());
        assertEquals("Gold", manager.getApplicableTier(50.00).getTierName());
    }
}

