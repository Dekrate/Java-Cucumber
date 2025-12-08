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

	private Teller teller;

    private Product orangeJuice;
    private Product milk;

    private LocalDate validDate;
    private LocalDate beforeValidDate;
    private LocalDate afterValidDate;

    @BeforeEach
    void setUp() {
	    SupermarketCatalog catalog = new FakeCatalog();
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
        assertTrue(receipt.getDiscounts().getFirst().description()
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

    @Test
    @DisplayName("Should throw exception for zero required quantity")
    void shouldThrowExceptionForZeroRequiredQuantity() {
        final String code = "TEST";
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 0, 6, discountPercentage,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for negative required quantity")
    void shouldThrowExceptionForNegativeRequiredQuantity() {
        final String code = "TEST";
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, -1, 6, discountPercentage,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for zero discounted quantity")
    void shouldThrowExceptionForZeroDiscountedQuantity() {
        final String code = "TEST";
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 6, 0, discountPercentage,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for negative discounted quantity")
    void shouldThrowExceptionForNegativeDiscountedQuantity() {
        final String code = "TEST";
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 6, -1, discountPercentage,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for negative discount percentage")
    void shouldThrowExceptionForNegativeDiscountPercentage() {
        final String code = "TEST";
        final BigDecimal negativeDiscount = bd(-10.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 6, 6, negativeDiscount,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for discount percentage over 100")
    void shouldThrowExceptionForDiscountPercentageOver100() {
        final String code = "TEST";
        final BigDecimal excessiveDiscount = bd(150.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 6, 6, excessiveDiscount,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception when valid until before valid from")
    void shouldThrowExceptionWhenValidUntilBeforeValidFrom() {
        final String code = "TEST";
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 15);
        final LocalDate validUntil = LocalDate.of(2025, 11, 13);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Coupon(code, orangeJuice, 6, 6, discountPercentage,
                validFrom, validUntil));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception when redeeming already redeemed coupon")
    void shouldThrowExceptionWhenRedeemingAlreadyRedeemedCoupon() {
        final BigDecimal discountPercentage = bd(50.0);
        final LocalDate validFrom = LocalDate.of(2025, 11, 13);
        final LocalDate validUntil = LocalDate.of(2025, 11, 15);

        Coupon coupon = new Coupon("TEST", orangeJuice, 6, 6, discountPercentage,
                validFrom, validUntil);
        coupon.redeem();

        Exception exception = assertThrows(IllegalStateException.class, coupon::redeem);
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should get valid from date")
    void shouldGetValidFromDate() {
        Coupon coupon = new Coupon("TEST", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        assertEquals(LocalDate.of(2025, 11, 13), coupon.getValidFrom());
    }

    @Test
    @DisplayName("Should get valid until date")
    void shouldGetValidUntilDate() {
        Coupon coupon = new Coupon("TEST", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        assertEquals(LocalDate.of(2025, 11, 15), coupon.getValidUntil());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        Coupon coupon1 = new Coupon("TEST", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        Coupon coupon2 = new Coupon("TEST", milk, 4, 4, bd(25.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        Coupon coupon3 = new Coupon("OTHER", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));

        assertEquals(coupon1, coupon2);
        assertNotEquals(coupon1, coupon3);
        assertNotEquals(null, coupon1);
    }

    @Test
    @DisplayName("Should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        Coupon coupon1 = new Coupon("TEST", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        Coupon coupon2 = new Coupon("TEST", milk, 4, 4, bd(25.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));

        assertEquals(coupon1.hashCode(), coupon2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        Coupon coupon = new Coupon("TEST123", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        String str = coupon.toString();

        assertTrue(str.contains("TEST123"));
        assertTrue(str.contains("orange juice"));
    }
}

