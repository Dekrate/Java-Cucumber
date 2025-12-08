package dojo.supermarket.model;

import dojo.supermarket.model.bundle.ProductBundle;
import dojo.supermarket.model.coupon.Coupon;
import dojo.supermarket.model.loyalty.LoyaltyCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integration Tests - Multiple Discount Types")
class IntegrationTest {

	private Teller teller;

    private Product toothbrush;
    private Product toothpaste;
    private Product apples;
    private Product orangeJuice;

    @BeforeEach
    void setUp() {
	    SupermarketCatalog catalog = new FakeCatalog();
        teller = new Teller(catalog);

        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);
        orangeJuice = new Product("orange juice", ProductUnit.EACH);

        catalog.addProduct(toothbrush, bd(0.99));
        catalog.addProduct(toothpaste, bd(1.79));
        catalog.addProduct(apples, bd(1.99));
        catalog.addProduct(orangeJuice, bd(2.50));
    }

    @Test
    @DisplayName("Should apply all discount types together")
    void shouldApplyAllDiscountTypesTogether() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);
        teller.addProductBundle(bundle);

        Coupon coupon = new Coupon(
            "OJ50",
            orangeJuice,
            6,
            6,
            bd(50.0),
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );
        teller.addCoupon(coupon);
        teller.setPurchaseDate(LocalDate.of(2025, 11, 14));

        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                apples, bd(20.0));

        LoyaltyCard card = new LoyaltyCard("LC123456", bd(100.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));
        cart.addItemQuantity(orangeJuice, bd(12));
        cart.addItemQuantity(apples, bd(2.0));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

        assertTrue(receipt.getDiscounts().size() >= 3,
                "Should have at least 3 discounts");

        assertTrue(card.getPoints().compareTo(bd(50.0)) > 0,
                "Should have earned points");

        BigDecimal originalTotal = bd(0.99).add(bd(1.79))
                .add(bd(12).multiply(bd(2.50)))
                .add(bd(2.0).multiply(bd(1.99)));
        assertTrue(receipt.getTotalPrice().compareTo(originalTotal) < 0);
    }

    @Test
    @DisplayName("Should handle complex shopping scenario")
    void shouldHandleComplexShoppingScenario() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));

        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 2);
        bundleProducts.put(toothpaste, 2);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Family Bundle", bundleProducts);
        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(5));
        cart.addItemQuantity(toothpaste, bd(3));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertNotNull(receipt);
	    assertFalse(receipt.getDiscounts().isEmpty());
        assertTrue(receipt.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should handle single item purchase with no discounts")
    void shouldHandleSingleItemPurchaseWithNoDiscounts() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(toothbrush);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(0.99), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
        assertEquals(1, receipt.getItems().size());
    }

    @Test
    @DisplayName("Should calculate correct totals with fractional quantities")
    void shouldCalculateCorrectTotalsWithFractionalQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                apples, bd(15.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.5));
        cart.addItemQuantity(apples, bd(1.5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(4.0).multiply(bd(1.99))
                .multiply(bd(0.85));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle expired coupon gracefully")
    void shouldHandleExpiredCouponGracefully() {
        Coupon expiredCoupon = new Coupon(
            "EXPIRED",
            orangeJuice,
            6,
            6,
            bd(50.0),
            LocalDate.of(2025, 11, 1),
            LocalDate.of(2025, 11, 5)
        );

        teller.addCoupon(expiredCoupon);
        teller.setPurchaseDate(LocalDate.of(2025, 11, 10));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(12));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(30.0), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should handle zero loyalty points usage")
    void shouldHandleZeroLoyaltyPointsUsage() {
        LoyaltyCard card = new LoyaltyCard("LC123");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0.0));

        assertBigDecimalEquals(bd(0.99), receipt.getTotalPrice());
        assertBigDecimalEquals(bd(0.99), card.getPoints());
    }
}

