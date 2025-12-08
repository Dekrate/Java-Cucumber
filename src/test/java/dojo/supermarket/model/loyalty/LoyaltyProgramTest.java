package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
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

        catalog.addProduct(bread, bd(2.00));
        catalog.addProduct(milk, bd(1.50));
        catalog.addProduct(apples, bd(3.00));
    }

    @Test
    @DisplayName("Should earn loyalty points from purchase")
    void shouldEarnLoyaltyPointsFromPurchase() {
        LoyaltyCard card = new LoyaltyCard("LC123456");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(2));
        cart.addItemQuantity(milk, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

        BigDecimal expectedTotal = bd(2).multiply(bd(2.00))
                .add(bd(1).multiply(bd(1.50)));
        assertBigDecimalEquals(expectedTotal, card.getPoints());
        assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should use loyalty points as payment")
    void shouldUseLoyaltyPointsAsPayment() {
        LoyaltyCard card = new LoyaltyCard("LC123456", bd(100.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

        BigDecimal expectedTotal = bd(4.00).subtract(bd(0.50));
        assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());

        BigDecimal expectedPoints = bd(100).subtract(bd(50))
                .add(expectedTotal);
        assertBigDecimalEquals(expectedPoints, card.getPoints());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should not use more points than available")
    void shouldNotUseMorePointsThanAvailable() {
        LoyaltyCard card = new LoyaltyCard("LC123456", bd(30.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

        BigDecimal expectedTotal = bd(4.00).subtract(bd(0.30));
        assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());
        assertBigDecimalEquals(expectedTotal, card.getPoints());
    }

    @Test
    @DisplayName("Should not use more points than total amount")
    void shouldNotUseMorePointsThanTotalAmount() {
        LoyaltyCard card = new LoyaltyCard("LC123456", bd(500.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(500.0));

        assertBigDecimalEquals(BigDecimal.ZERO, receipt.getTotalPrice());
        assertBigDecimalEquals(bd(300.0), card.getPoints());
    }

    @Test
    @DisplayName("Should earn points with discounts applied")
    void shouldEarnPointsWithDiscountsApplied() {
        LoyaltyCard card = new LoyaltyCard("LC123456");
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                milk, bd(10.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, bd(4));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

        BigDecimal expected = bd(4).multiply(bd(1.50))
                .multiply(bd(0.9));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertBigDecimalEquals(expected, card.getPoints());
    }

    @Test
    @DisplayName("Should combine loyalty points with other discounts")
    void shouldCombineLoyaltyPointsWithOtherDiscounts() {
        LoyaltyCard card = new LoyaltyCard("LC123456", bd(100.0));
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                apples, bd(20.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.0));
        cart.addItemQuantity(bread, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

        BigDecimal applesTotal = bd(2).multiply(bd(3.00))
                .multiply(bd(0.8));
        BigDecimal beforeLoyalty = applesTotal.add(bd(2.00));
        BigDecimal expectedTotal = beforeLoyalty.subtract(bd(0.50));
        assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());

        BigDecimal expectedPoints = bd(100).subtract(bd(50))
                .add(expectedTotal);
        assertBigDecimalEquals(expectedPoints, card.getPoints());
    }

    @Test
    @DisplayName("Should handle zero initial points")
    void shouldHandleZeroInitialPoints() {
        LoyaltyCard card = new LoyaltyCard("LC123456");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

        assertBigDecimalEquals(bd(2.00), receipt.getTotalPrice());
        assertBigDecimalEquals(bd(2.00), card.getPoints());
    }
}

