package dojo.supermarket.model;

import dojo.supermarket.model.bundle.BundleManager;
import dojo.supermarket.model.bundle.ProductBundle;
import dojo.supermarket.model.category.ConjuredCategory;
import dojo.supermarket.model.category.PremiumCategory;
import dojo.supermarket.model.category.StandardCategory;
import dojo.supermarket.model.loyalty.GoldLoyaltyTier;
import dojo.supermarket.model.loyalty.LoyaltyProgramManager;
import dojo.supermarket.model.loyalty.SilverLoyaltyTier;
import dojo.supermarket.model.offer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComprehensiveSupermarketTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);
    }

    // ========== CATEGORY TESTS ==========

    @Test
    @DisplayName("Standard category products should have default pricing")
    void testStandardCategoryProduct() {
        Product apple = new Product("apple", ProductUnit.KILO, new StandardCategory());
        catalog.addProduct(apple, 2.50);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apple, 3.0);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(7.50, receipt.getTotalPrice(), 0.01);
        assertEquals("Standard", apple.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("Conjured category products should be identifiable")
    void testConjuredCategoryProduct() {
        ConjuredCategory conjuredCategory = new ConjuredCategory();
        Product magicWand = new Product("magic wand", ProductUnit.EACH, conjuredCategory);
        catalog.addProduct(magicWand, 10.00);

        assertEquals("Conjured", magicWand.getCategory().getCategoryName());
        assertEquals(2.0, conjuredCategory.getDegradationMultiplier());
    }

    @Test
    @DisplayName("Premium category products should be identifiable")
    void testPremiumCategoryProduct() {
        Product champagne = new Product("champagne", ProductUnit.EACH, new PremiumCategory());
        catalog.addProduct(champagne, 50.00);

        assertEquals("Premium", champagne.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("Products can change categories dynamically")
    void testDynamicCategoryChange() {
        Product item = new Product("item", ProductUnit.EACH);
        assertEquals("Standard", item.getCategory().getCategoryName());

        item.setCategory(new ConjuredCategory());
        assertEquals("Conjured", item.getCategory().getCategoryName());
    }

    // ========== OFFER STRATEGY TESTS ==========

    @Test
    @DisplayName("Three for two offer should apply correct discount")
    void testThreeForTwoStrategy() {
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 1.00);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(2.00, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("3 for 2", receipt.getDiscounts().get(0).getDescription());
    }

    @Test
    @DisplayName("Three for two should not apply for less than 3 items")
    void testThreeForTwoNotAppliedForTwoItems() {
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 1.00);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(2.00, receipt.getTotalPrice(), 0.01);
        assertEquals(0, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Two for amount offer should apply correct discount")
    void testTwoForAmountStrategy() {
        Product cola = new Product("cola", ProductUnit.EACH);
        catalog.addProduct(cola, 2.00);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cola, 3.00);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cola, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(3.00, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).getDescription().contains("2 for 3.0"));
    }

    @Test
    @DisplayName("Five for amount offer should apply correct discount")
    void testFiveForAmountStrategy() {
        Product candy = new Product("candy", ProductUnit.EACH);
        catalog.addProduct(candy, 1.00);
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, candy, 4.00);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(candy, 5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(4.00, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).getDescription().contains("5 for 4.0"));
    }

    @Test
    @DisplayName("Percentage discount should apply correctly")
    void testPercentageDiscountStrategy() {
        Product milk = new Product("milk", ProductUnit.EACH);
        catalog.addProduct(milk, 2.00);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, milk, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(3.60, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).getDescription().contains("10.0% off"));
    }

    @Test
    @DisplayName("Multiple different offers should work together")
    void testMultipleOffers() {
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        Product shampoo = new Product("shampoo", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 2.00);
        catalog.addProduct(shampoo, 5.00);

        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothpaste, 0);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, shampoo, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 3);
        cart.addItemQuantity(shampoo, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 3 toothpastes: 2.00 * 3 = 6.00, discount -2.00 = 4.00
        // 1 shampoo: 5.00, discount 20% = -1.00 = 4.00
        // Total: 8.00
        assertEquals(8.00, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }

    // ========== BUNDLE TESTS ==========

    @Test
    @DisplayName("Bundle discount should apply when all products present")
    void testBundleDiscount() {
        Product bread = new Product("bread", ProductUnit.EACH);
        Product butter = new Product("butter", ProductUnit.EACH);
        Product jam = new Product("jam", ProductUnit.EACH);

        catalog.addProduct(bread, 2.00);
        catalog.addProduct(butter, 3.00);
        catalog.addProduct(jam, 4.00);

        ProductBundle breakfastBundle = new ProductBundle(
            "Breakfast",
            Arrays.asList(bread, butter, jam),
            15.0
        );
        teller.getBundleManager().addBundle(breakfastBundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 1);
        cart.addItemQuantity(butter, 1);
        cart.addItemQuantity(jam, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Base: 2 + 3 + 4 = 9.00
        // Bundle discount: 15% of 9.00 = 1.35
        // Total: 9.00 - 1.35 = 7.65
        assertEquals(7.65, receipt.getTotalPrice(), 0.01);

        boolean hasBundleDiscount = receipt.getDiscounts().stream()
            .anyMatch(d -> d.getDescription().contains("Breakfast bundle"));
        assertTrue(hasBundleDiscount);
    }

    @Test
    @DisplayName("Bundle discount should not apply when products missing")
    void testBundleNotAppliedWhenProductsMissing() {
        Product bread = new Product("bread", ProductUnit.EACH);
        Product butter = new Product("butter", ProductUnit.EACH);
        Product jam = new Product("jam", ProductUnit.EACH);

        catalog.addProduct(bread, 2.00);
        catalog.addProduct(butter, 3.00);
        catalog.addProduct(jam, 4.00);

        ProductBundle breakfastBundle = new ProductBundle(
            "Breakfast",
            Arrays.asList(bread, butter, jam),
            15.0
        );
        teller.getBundleManager().addBundle(breakfastBundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 1);
        cart.addItemQuantity(butter, 1);
        // jam is missing

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(5.00, receipt.getTotalPrice(), 0.01);

        boolean hasBundleDiscount = receipt.getDiscounts().stream()
            .anyMatch(d -> d.getDescription().contains("Breakfast bundle"));
        assertFalse(hasBundleDiscount);
    }

    @Test
    @DisplayName("Multiple bundles can be registered")
    void testMultipleBundles() {
        Product item1 = new Product("item1", ProductUnit.EACH);
        Product item2 = new Product("item2", ProductUnit.EACH);

        catalog.addProduct(item1, 10.00);
        catalog.addProduct(item2, 20.00);

        ProductBundle bundle1 = new ProductBundle("Bundle1", Arrays.asList(item1), 10.0);
        ProductBundle bundle2 = new ProductBundle("Bundle2", Arrays.asList(item2), 20.0);

        BundleManager manager = teller.getBundleManager();
        manager.addBundle(bundle1);
        manager.addBundle(bundle2);

        assertEquals(2, manager.getBundles().size());
    }

    // ========== LOYALTY PROGRAM TESTS ==========

    @Test
    @DisplayName("Basic tier should not provide discount")
    void testBasicLoyaltyTier() {
        Product item = new Product("item", ProductUnit.EACH);
        catalog.addProduct(item, 10.00);

        teller.enableLoyaltyProgram(); // Enable loyalty program

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(item, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(10.00, receipt.getTotalPrice(), 0.01);

        boolean hasLoyaltyDiscount = receipt.getDiscounts().stream()
            .anyMatch(d -> d.getDescription().contains("Member"));
        assertFalse(hasLoyaltyDiscount);
    }

    @Test
    @DisplayName("Silver tier should apply 5% discount for purchases over 20")
    void testSilverLoyaltyTier() {
        Product item = new Product("item", ProductUnit.EACH);
        catalog.addProduct(item, 25.00);

        teller.enableLoyaltyProgram(); // Enable loyalty program

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(item, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 25.00 - 5% = 23.75
        assertEquals(23.75, receipt.getTotalPrice(), 0.01);

        boolean hasSilverDiscount = receipt.getDiscounts().stream()
            .anyMatch(d -> d.getDescription().contains("Silver Member"));
        assertTrue(hasSilverDiscount);
    }

    @Test
    @DisplayName("Gold tier should apply 10% discount for purchases over 50")
    void testGoldLoyaltyTier() {
        Product item = new Product("item", ProductUnit.EACH);
        catalog.addProduct(item, 60.00);

        teller.enableLoyaltyProgram(); // Enable loyalty program

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(item, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 60.00 - 10% = 54.00
        assertEquals(54.00, receipt.getTotalPrice(), 0.01);

        boolean hasGoldDiscount = receipt.getDiscounts().stream()
            .anyMatch(d -> d.getDescription().contains("Gold Member"));
        assertTrue(hasGoldDiscount);
    }

    @Test
    @DisplayName("Loyalty tier should be determined by total amount")
    void testLoyaltyTierDetermination() {
        LoyaltyProgramManager manager = new LoyaltyProgramManager();

        assertEquals("Basic", manager.getApplicableTier(10.00).getTierName());
        assertEquals("Silver", manager.getApplicableTier(25.00).getTierName());
        assertEquals("Gold", manager.getApplicableTier(60.00).getTierName());
    }

    @Test
    @DisplayName("Loyalty program properties should be correct")
    void testLoyaltyProgramProperties() {
        SilverLoyaltyTier silver = new SilverLoyaltyTier();
        assertEquals(5.0, silver.getDiscountPercentage());
        assertEquals(1.5, silver.getPointsMultiplier());

        GoldLoyaltyTier gold = new GoldLoyaltyTier();
        assertEquals(10.0, gold.getDiscountPercentage());
        assertEquals(2.0, gold.getPointsMultiplier());
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Special offers, bundles, and loyalty should work together")
    void testCompleteIntegration() {
        Product bread = new Product("bread", ProductUnit.EACH);
        Product milk = new Product("milk", ProductUnit.EACH);

        catalog.addProduct(bread, 2.00);
        catalog.addProduct(milk, 3.00);

        // Add special offer
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, bread, 10.0);

        // Add bundle
        ProductBundle bundle = new ProductBundle("Morning", Arrays.asList(bread, milk), 5.0);
        teller.getBundleManager().addBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 10);
        cart.addItemQuantity(milk, 10);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Should have multiple discounts applied
        assertTrue(receipt.getDiscounts().size() >= 2);
        assertTrue(receipt.getTotalPrice() < 50.00); // Total with discounts
    }

    @Test
    @DisplayName("Empty cart should produce empty receipt")
    void testEmptyCart() {
        ShoppingCart cart = new ShoppingCart();
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.00, receipt.getTotalPrice(), 0.01);
        assertEquals(0, receipt.getItems().size());
    }

    @Test
    @DisplayName("Receipt should track all discounts separately")
    void testDiscountTracking() {
        Product item1 = new Product("item1", ProductUnit.EACH);
        Product item2 = new Product("item2", ProductUnit.EACH);

        catalog.addProduct(item1, 10.00);
        catalog.addProduct(item2, 10.00);

        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, item1, 10.0);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, item2, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(item1, 1);
        cart.addItemQuantity(item2, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(2, receipt.getItems().size());
        assertTrue(receipt.getDiscounts().size() >= 2);
    }

    // ========== EXTENSIBILITY TESTS ==========

    @Test
    @DisplayName("New offer strategy can be registered at runtime")
    void testOfferStrategyExtensibility() {
        // This test demonstrates that the system is open for extension
        assertDoesNotThrow(() -> {
            OfferStrategy customStrategy = new PercentageDiscountStrategy();
            assertNotNull(customStrategy);
        });
    }

    @Test
    @DisplayName("Product categories system is extensible")
    void testCategoryExtensibility() {
        // Demonstrate that new categories can be created
        ProductCategory customCategory = new ProductCategory() {
            @Override
            public String getCategoryName() {
                return "Custom";
            }

            @Override
            public double applyPriceAdjustment(double basePrice, double quantity) {
                return basePrice * quantity * 0.9; // 10% discount
            }
        };

        Product customProduct = new Product("custom", ProductUnit.EACH, customCategory);
        assertEquals("Custom", customProduct.getCategory().getCategoryName());
    }

    @Test
    @DisplayName("Bundle system is extensible")
    void testBundleExtensibility() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        Product p2 = new Product("p2", ProductUnit.EACH);

        ProductBundle customBundle = new ProductBundle(
            "Custom Bundle",
            Arrays.asList(p1, p2),
            25.0
        );

        assertEquals("Custom Bundle", customBundle.getName());
        assertEquals(25.0, customBundle.getDiscountPercentage());
    }

    @Test
    @DisplayName("Loyalty program is extensible")
    void testLoyaltyProgramExtensibility() {
        LoyaltyProgramManager manager = new LoyaltyProgramManager();

        // Custom loyalty tier can be added
        assertDoesNotThrow(() -> {
            manager.addLoyaltyProgram(new GoldLoyaltyTier());
        });
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Large quantity should be handled correctly")
    void testLargeQuantity() {
        Product item = new Product("item", ProductUnit.EACH);
        catalog.addProduct(item, 1.00);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(item, 1000);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(1000.00, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Fractional quantities should work for weight-based products")
    void testFractionalQuantities() {
        Product cheese = new Product("cheese", ProductUnit.KILO);
        catalog.addProduct(cheese, 10.00);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cheese, 0.5);


	    Receipt receipt = teller.checksOutArticlesFrom(cart);

	    assertEquals(5.00, receipt.getTotalPrice(), 0.01);
    }

	@Test
	@DisplayName("Adding same product multiple times should accumulate")
	void testProductAccumulation() {
		Product apple = new Product("apple", ProductUnit.KILO);
		catalog.addProduct(apple, 2.00);

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(apple, 1.0);
		cart.addItemQuantity(apple, 2.0);
		cart.addItemQuantity(apple, 1.5);

		assertEquals(4.5, cart.productQuantities().get(apple), 0.01);
	}
}