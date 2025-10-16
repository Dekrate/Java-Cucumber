package dojo.supermarket.model.category;

import dojo.supermarket.model.ProductCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryTest {

    @Test
    @DisplayName("StandardCategory should apply no adjustment")
    void testStandardCategory() {
        ProductCategory category = new StandardCategory();

        assertEquals("Standard", category.getCategoryName());
        assertEquals(10.00, category.applyPriceAdjustment(2.00, 5.0), 0.01);
        assertFalse(category.hasBundleRules());
    }

    @Test
    @DisplayName("ConjuredCategory should have degradation multiplier")
    void testConjuredCategory() {
        ConjuredCategory category = new ConjuredCategory();

        assertEquals("Conjured", category.getCategoryName());
        assertEquals(2.0, category.getDegradationMultiplier());
        assertEquals(15.00, category.applyPriceAdjustment(3.00, 5.0), 0.01);
        assertFalse(category.hasBundleRules());
    }

    @Test
    @DisplayName("PremiumCategory should work correctly")
    void testPremiumCategory() {
        ProductCategory category = new PremiumCategory();

        assertEquals("Premium", category.getCategoryName());
        assertEquals(50.00, category.applyPriceAdjustment(10.00, 5.0), 0.01);
        assertFalse(category.hasBundleRules());
    }
}

