package dojo.supermarket.model.coupon;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponManager Tests")
class CouponManagerTest {

    private CouponManager couponManager;
    private SupermarketCatalog catalog;
    private Product orangeJuice;
    private Product milk;
    private LocalDate validDate;

    @BeforeEach
    void setUp() {
        couponManager = new CouponManager();
        catalog = new FakeCatalog();

        orangeJuice = new Product("orange juice", ProductUnit.EACH);
        milk = new Product("milk", ProductUnit.EACH);

        catalog.addProduct(orangeJuice, bd(2.50));
        catalog.addProduct(milk, bd(1.20));

        validDate = LocalDate.of(2025, 11, 14);
    }

    @Test
    @DisplayName("Should clear all coupons")
    void shouldClearAllCoupons() {
        Coupon coupon1 = new Coupon("COUPON1", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        Coupon coupon2 = new Coupon("COUPON2", milk, 4, 4, bd(25.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));

        couponManager.addCoupon(coupon1);
        couponManager.addCoupon(coupon2);

        assertTrue(couponManager.hasAvailableCoupons());

        couponManager.clearCoupons();

        assertFalse(couponManager.hasAvailableCoupons());
    }

    @Test
    @DisplayName("Should get available coupons")
    void shouldGetAvailableCoupons() {
        Coupon coupon1 = new Coupon("COUPON1", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));
        Coupon coupon2 = new Coupon("COUPON2", milk, 4, 4, bd(25.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));

        couponManager.addCoupon(coupon1);
        couponManager.addCoupon(coupon2);

        List<Coupon> available = couponManager.getAvailableCoupons();
        assertEquals(2, available.size());
        assertTrue(available.contains(coupon1));
        assertTrue(available.contains(coupon2));

        Map<Product, BigDecimal> productQuantities = new HashMap<>();
        productQuantities.put(orangeJuice, bd(12));
        couponManager.applyCoupons(productQuantities, catalog, validDate);

        available = couponManager.getAvailableCoupons();
        assertEquals(1, available.size());
        assertFalse(available.contains(coupon1));
        assertTrue(available.contains(coupon2));
    }

    @Test
    @DisplayName("Should check if it has available coupons")
    void shouldCheckIfHasAvailableCoupons() {
        assertFalse(couponManager.hasAvailableCoupons());

        Coupon coupon = new Coupon("COUPON1", orangeJuice, 6, 6, bd(50.0),
                LocalDate.of(2025, 11, 13),
                LocalDate.of(2025, 11, 15));

        couponManager.addCoupon(coupon);
        assertTrue(couponManager.hasAvailableCoupons());

        Map<Product, BigDecimal> productQuantities = new HashMap<>();
        productQuantities.put(orangeJuice, bd(12));
        couponManager.applyCoupons(productQuantities, catalog, validDate);

        assertFalse(couponManager.hasAvailableCoupons());
    }
}

