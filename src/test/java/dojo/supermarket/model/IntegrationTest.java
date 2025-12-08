package dojo.supermarket.model;

import dojo.supermarket.model.bundle.ProductBundle;
import dojo.supermarket.model.coupon.Coupon;
import dojo.supermarket.model.loyalty.LoyaltyCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration Tests - Multiple Discount Types")
class IntegrationTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    private Product toothbrush;
    private Product toothpaste;
    private Product apples;
    private Product orangeJuice;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);

        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);
        orangeJuice = new Product("orange juice", ProductUnit.EACH);

        catalog.addProduct(toothbrush, 0.99);
        catalog.addProduct(toothpaste, 1.79);
        catalog.addProduct(apples, 1.99);
        catalog.addProduct(orangeJuice, 2.50);
    }

    @Test
    @DisplayName("Should apply all discount types together")
    void shouldApplyAllDiscountTypesTogether() {
        // Setup bundle discount
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);
        teller.addProductBundle(bundle);

        // Setup coupon
        Coupon coupon = new Coupon(
            "OJ50",
            orangeJuice,
            6,
            6,
            50.0,
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );
        teller.addCoupon(coupon);
        teller.setPurchaseDate(LocalDate.of(2025, 11, 14));

        // Setup regular offer
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        // Setup loyalty card
        LoyaltyCard card = new LoyaltyCard("LC123456", 100.0);

        // Create cart with all products
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);  // Part of bundle
        cart.addItemQuantity(toothpaste, 1);  // Part of bundle
        cart.addItemQuantity(orangeJuice, 12); // Coupon applies
        cart.addItemQuantity(apples, 2.0);     // Regular discount

        // Checkout with loyalty points
        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 50.0);

        // Verify multiple discounts applied
        assertTrue(receipt.getDiscounts().size() >= 3, "Should have at least 3 discounts");

        // Verify loyalty points earned
        assertTrue(card.getPoints() > 50.0, "Should have earned points");

        // Total should be less than original price due to all discounts
        double originalTotal = 0.99 + 1.79 + (12 * 2.50) + (2.0 * 1.99);
        assertTrue(receipt.getTotalPrice() < originalTotal);
    }

    @Test
    @DisplayName("Should handle complex shopping scenario")
    void shouldHandleComplexShoppingScenario() {
        // 3-for-2 on toothbrush
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        // Bundle discount
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 2);
        bundleProducts.put(toothpaste, 2);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Family Bundle", bundleProducts);
        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 5);
        cart.addItemQuantity(toothpaste, 3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertNotNull(receipt);
        assertTrue(receipt.getDiscounts().size() > 0);
        assertTrue(receipt.getTotalPrice() > 0);
    }

    @Test
    @DisplayName("Should handle single item purchase with no discounts")
    void shouldHandleSingleItemPurchaseWithNoDiscounts() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(toothbrush);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
        assertEquals(1, receipt.getItems().size());
    }

    @Test
    @DisplayName("Should calculate correct totals with fractional quantities")
    void shouldCalculateCorrectTotalsWithFractionalQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 15.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(apples, 1.5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 4.0 kg at 1.99 = 7.96, with 15% off = 6.766
        assertEquals(6.766, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should handle expired coupon gracefully")
    void shouldHandleExpiredCouponGracefully() {
        Coupon expiredCoupon = new Coupon(
            "EXPIRED",
            orangeJuice,
            6,
            6,
            50.0,
            LocalDate.of(2025, 11, 1),
            LocalDate.of(2025, 11, 5)
        );

        teller.addCoupon(expiredCoupon);
        teller.setPurchaseDate(LocalDate.of(2025, 11, 10));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Coupon should not apply
        assertEquals(30.0, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should handle zero loyalty points usage")
    void shouldHandleZeroLoyaltyPointsUsage() {
        LoyaltyCard card = new LoyaltyCard("LC123");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 0.0);

        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
        assertEquals(0.99, card.getPoints(), 0.01);
    }
}

