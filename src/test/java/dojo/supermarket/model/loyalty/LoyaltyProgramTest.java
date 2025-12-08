package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Loyalty Program Tests")
class LoyaltyProgramTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    private Product bread;
    private Product milk;
    private Product apples;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);

        bread = new Product("bread", ProductUnit.EACH);
        milk = new Product("milk", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);

        catalog.addProduct(bread, 2.00);
        catalog.addProduct(milk, 1.50);
        catalog.addProduct(apples, 3.00);
    }

    @Test
    @DisplayName("Should earn loyalty points from purchase")
    void shouldEarnLoyaltyPointsFromPurchase() {
        LoyaltyCard card = new LoyaltyCard("LC123456");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 2);
        cart.addItemQuantity(milk, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 0);

        // Total: 2*2.00 + 1*1.50 = 5.50
        // Points earned: 5.50 * 1.0 = 5.50
        assertEquals(5.50, card.getPoints(), 0.01);
        assertEquals(5.50, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should use loyalty points as payment")
    void shouldUseLoyaltyPointsAsPayment() {
        LoyaltyCard card = new LoyaltyCard("LC123456", 100.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 50.0);

        // Total before points: 4.00
        // Points used: 50 points = 50 * 0.01 = 0.50
        // Total after points: 4.00 - 0.50 = 3.50
        // Remaining points: 100 - 50 = 50
        // Points earned from this purchase: 3.50 * 1.0 = 3.50
        // Final points: 50 + 3.50 = 53.50
        assertEquals(3.50, receipt.getTotalPrice(), 0.01);
        assertEquals(53.50, card.getPoints(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should not use more points than available")
    void shouldNotUseMorePointsThanAvailable() {
        LoyaltyCard card = new LoyaltyCard("LC123456", 30.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 50.0);

        // Total before points: 4.00
        // Requested 50 points but only has 30
        // Points used: 30 points = 30 * 0.01 = 0.30
        // Total after points: 4.00 - 0.30 = 3.70
        // Points earned: 3.70 * 1.0 = 3.70
        // Final points: 0 + 3.70 = 3.70
        assertEquals(3.70, receipt.getTotalPrice(), 0.01);
        assertEquals(3.70, card.getPoints(), 0.01);
    }

    @Test
    @DisplayName("Should not use more points than total amount")
    void shouldNotUseMorePointsThanTotalAmount() {
        LoyaltyCard card = new LoyaltyCard("LC123456", 500.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 500.0);

        // Total: 2.00
        // Maximum points usable: 2.00 / 0.01 = 200 points
        // Points used: 200 points = 200 * 0.01 = 2.00
        // Total after points: 0.00
        // Remaining points: 500 - 200 = 300
        // Points earned: 0 (because total is 0)
        // Final points: 300
        assertEquals(0.00, receipt.getTotalPrice(), 0.01);
        assertEquals(300.0, card.getPoints(), 0.01);
    }

    @Test
    @DisplayName("Should earn points with discounts applied")
    void shouldEarnPointsWithDiscountsApplied() {
        LoyaltyCard card = new LoyaltyCard("LC123456");
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, milk, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, 4);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 0);

        // Total before discount: 4 * 1.50 = 6.00
        // Discount: 10% = 0.60
        // Total after discount: 5.40
        // Points earned: 5.40
        assertEquals(5.40, receipt.getTotalPrice(), 0.01);
        assertEquals(5.40, card.getPoints(), 0.01);
    }

    @Test
    @DisplayName("Should combine loyalty points with other discounts")
    void shouldCombineLoyaltyPointsWithOtherDiscounts() {
        LoyaltyCard card = new LoyaltyCard("LC123456", 100.0);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.0);
        cart.addItemQuantity(bread, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 50.0);

        // Apples: 2 * 3.00 = 6.00, with 20% off = 4.80
        // Bread: 2.00
        // Total before loyalty: 6.80
        // Loyalty discount: 50 points = 0.50
        // Total after loyalty: 6.30
        // Points earned: 6.30
        // Final points: 100 - 50 + 6.30 = 56.30
        assertEquals(6.30, receipt.getTotalPrice(), 0.01);
        assertEquals(56.30, card.getPoints(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should handle checkout without loyalty card")
    void shouldHandleCheckoutWithoutLoyaltyCard() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, null, 0);

        assertEquals(2.00, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should not use points when zero points requested")
    void shouldNotUsePointsWhenZeroPointsRequested() {
        LoyaltyCard card = new LoyaltyCard("LC123456", 100.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, 0);

        // Points earned: 2.00
        // Final points: 100 + 2.00 = 102.00
        assertEquals(2.00, receipt.getTotalPrice(), 0.01);
        assertEquals(102.0, card.getPoints(), 0.01);
    }

    @Test
    @DisplayName("Should throw exception for invalid loyalty card operations")
    void shouldThrowExceptionForInvalidOperations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoyaltyCard("LC123", -10.0);
        });

        LoyaltyCard card = new LoyaltyCard("LC123");

        assertThrows(IllegalArgumentException.class, () -> {
            card.addPoints(-5.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            card.usePoints(-5.0);
        });
    }

    @Test
    @DisplayName("Should not allow using more points than available")
    void shouldNotAllowUsingMorePointsThanAvailable() {
        LoyaltyCard card = new LoyaltyCard("LC123", 10.0);

        boolean result = card.usePoints(20.0);

        assertFalse(result);
        assertEquals(10.0, card.getPoints(), 0.01);
    }
}

