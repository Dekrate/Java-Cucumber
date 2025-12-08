package dojo.supermarket.model.coupon;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

        catalog.addProduct(orangeJuice, 2.50);
        catalog.addProduct(milk, 1.20);

        validDate = LocalDate.of(2025, 11, 14);
        beforeValidDate = LocalDate.of(2025, 11, 12);
        afterValidDate = LocalDate.of(2025, 11, 16);
    }

    @Test
    @DisplayName("Should apply coupon when valid and sufficient quantity")
    void shouldApplyCouponWhenValidAndSufficientQuantity() {
        // Buy 6 bottles, get 6 more at 50% off (valid 13/11 - 15/11)
        Coupon coupon = new Coupon(
            "OJ50",
            orangeJuice,
            6, // required quantity
            6, // discounted quantity
            50.0, // 50% off
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );

        teller.addCoupon(coupon);
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 12 bottles at 2.50 = 30.00
        // Discount: 6 bottles at 50% off = 6 * 2.50 * 0.50 = 7.50
        // Total: 30.00 - 7.50 = 22.50
        assertEquals(22.50, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).description().contains("OJ50"));
    }

    @Test
    @DisplayName("Should not apply coupon when insufficient quantity")
    void shouldNotApplyCouponWhenInsufficientQuantity() {
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
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 10); // Only 10, need 12

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(25.00, receipt.getTotalPrice(), 0.01);
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
            50.0,
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );

        teller.addCoupon(coupon);
        teller.setPurchaseDate(beforeValidDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(30.00, receipt.getTotalPrice(), 0.01);
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
            50.0,
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );

        teller.addCoupon(coupon);
        teller.setPurchaseDate(afterValidDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(30.00, receipt.getTotalPrice(), 0.01);
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
            50.0,
            LocalDate.of(2025, 11, 13),
            LocalDate.of(2025, 11, 15)
        );

        teller.addCoupon(coupon);
        teller.setPurchaseDate(validDate);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);

        assertFalse(coupon.isRedeemed());

        teller.checksOutArticlesFrom(cart);

        assertTrue(coupon.isRedeemed());
    }

    @Test
    @DisplayName("Should not apply already redeemed coupon")
    void shouldNotApplyAlreadyRedeemedCoupon() {
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
        teller.setPurchaseDate(validDate);

        // First purchase
        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItemQuantity(orangeJuice, 12);
        Receipt receipt1 = teller.checksOutArticlesFrom(cart1);

        assertEquals(22.50, receipt1.getTotalPrice(), 0.01);
        assertTrue(coupon.isRedeemed());

        // Second purchase - coupon should not apply
        ShoppingCart cart2 = new ShoppingCart();
        cart2.addItemQuantity(orangeJuice, 12);
        Receipt receipt2 = teller.checksOutArticlesFrom(cart2);

        assertEquals(30.00, receipt2.getTotalPrice(), 0.01);
        assertTrue(receipt2.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply coupon on valid date boundaries")
    void shouldApplyCouponOnValidDateBoundaries() {
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

        // Test on first valid date
        teller.setPurchaseDate(LocalDate.of(2025, 11, 13));
        ShoppingCart cart1 = new ShoppingCart();
        cart1.addItemQuantity(orangeJuice, 12);
        Receipt receipt1 = teller.checksOutArticlesFrom(cart1);

        assertEquals(22.50, receipt1.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should combine coupon with other discounts")
    void shouldCombineCouponWithOtherDiscounts() {
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
        teller.setPurchaseDate(validDate);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, milk, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(orangeJuice, 12);
        cart.addItemQuantity(milk, 3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Orange juice: 22.50 (with coupon)
        // Milk: 3 * 1.20 = 3.60, with 10% off = 3.24
        // Total: 22.50 + 3.24 = 25.74
        assertEquals(25.74, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should throw exception when creating invalid coupon")
    void shouldThrowExceptionWhenCreatingInvalidCoupon() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Coupon("INVALID", orangeJuice, 0, 6, 50.0,
                validDate, validDate.plusDays(1));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Coupon("INVALID", orangeJuice, 6, -1, 50.0,
                validDate, validDate.plusDays(1));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Coupon("INVALID", orangeJuice, 6, 6, 150.0,
                validDate, validDate.plusDays(1));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Coupon("INVALID", orangeJuice, 6, 6, 50.0,
                validDate.plusDays(2), validDate);
        });
    }
}

