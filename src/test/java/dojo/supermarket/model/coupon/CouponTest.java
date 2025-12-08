package dojo.supermarket.model.coupon;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coupon Tests")
class CouponTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    private Product orangeJuice;
    private Product milk;

    private LocalDate validDate;
    private LocalDate beforeValidDate;
    private LocalDate afterValidDate;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);

        orangeJuice = new Product("orange juice", ProductUnit.EACH);
        milk = new Product("milk", ProductUnit.EACH);

        catalog.addProduct(orangeJuice, bd(2.50));
        catalog.addProduct(milk, bd(1.20));

        validDate = LocalDate.of(2025, 11, 14);
        beforeValidDate = LocalDate.of(2025, 11, 12);
        afterValidDate = LocalDate.of(2025, 11, 16);
    }

    @Test
    @DisplayName("Should apply coupon when valid and sufficient quantity")
    void shouldApplyCouponWhenValidAndSufficientQuantity() {
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
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(12));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(12).multiply(bd(2.50))
                .subtract(bd(6).multiply(bd(2.50)).multiply(bd(0.50)));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).description()
                .contains("OJ50"));
    }

    @Test
    @DisplayName("Should not apply coupon when insufficient quantity")
    void shouldNotApplyCouponWhenInsufficientQuantity() {
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
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(10));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(25.00), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
        assertFalse(coupon.isRedeemed());
    }

    @Test
    @DisplayName("Should not apply coupon before valid date")
    void shouldNotApplyCouponBeforeValidDate() {
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
        teller.setPurchaseDate(beforeValidDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(12));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(30.00), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
        assertFalse(coupon.isRedeemed());
    }

    @Test
    @DisplayName("Should not apply coupon after valid date")
    void shouldNotApplyCouponAfterValidDate() {
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
        teller.setPurchaseDate(afterValidDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(12));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(30.00), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
        assertFalse(coupon.isRedeemed());
    }

    @Test
    @DisplayName("Should mark coupon as redeemed after use")
    void shouldMarkCouponAsRedeemedAfterUse() {
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
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, bd(12));

        teller.checksOutArticlesFrom(cart);

        assertTrue(coupon.isRedeemed());
    }

    @Test
    @DisplayName("Should not apply coupon twice")
    void shouldNotApplyCouponTwice() {
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
        teller.setPurchaseDate(validDate);

        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItemQuantity(orangeJuice, bd(12));
        teller.checksOutArticlesFrom(cart1);

        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItemQuantity(orangeJuice, bd(12));
        Receipt receipt2 = teller.checksOutArticlesFrom(cart2);

        assertBigDecimalEquals(bd(30.00), receipt2.getTotalPrice());
        assertTrue(receipt2.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should handle different discount percentages")
    void shouldHandleDifferentDiscountPercentages() {
        Coupon coupon = new Coupon(
            "MILK25",
            milk,
            4,
            2,
            bd(25.0),
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );

        teller.addCoupon(coupon);
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(milk, bd(6));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(6).multiply(bd(1.20))
                .subtract(bd(2).multiply(bd(1.20)).multiply(bd(0.25)));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }
}

