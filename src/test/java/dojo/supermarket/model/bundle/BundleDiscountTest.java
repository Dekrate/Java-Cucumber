package dojo.supermarket.model.bundle;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Bundle Discount Tests")
class BundleDiscountTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    private Product toothbrush;
    private Product toothpaste;
    private Product apples;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);

        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);

        catalog.addProduct(toothbrush, bd(0.99));
        catalog.addProduct(toothpaste, bd(1.79));
        catalog.addProduct(apples, bd(1.99));
    }

    @Test
    @DisplayName("Should apply bundle discount when all products are purchased")
    void shouldApplyBundleDiscountWhenComplete() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(0.99).add(bd(1.79))
                .multiply(bd(0.9));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());

        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().get(0).description()
                .contains("Dental Care Bundle"));
    }

    @Test
    @DisplayName("Should not apply bundle discount when bundle is incomplete")
    void shouldNotApplyBundleDiscountWhenIncomplete() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(0.99), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply bundle discount multiple times")
    void shouldApplyBundleDiscountMultipleTimes() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(2));
        cart.addItemQuantity(toothpaste, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal bundlePrice = bd(0.99).add(bd(1.79));
        BigDecimal expected = bundlePrice.multiply(bd(2))
                .multiply(bd(0.9));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply bundle discount only for complete bundles")
    void shouldApplyBundleDiscountOnlyForCompleteBundles() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(3));
        cart.addItemQuantity(toothpaste, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal totalBefore = bd(3).multiply(bd(0.99))
                .add(bd(2).multiply(bd(1.79)));
        BigDecimal bundleDiscount = bd(0.99).add(bd(1.79))
                .multiply(bd(0.1))
                .multiply(bd(2));
        BigDecimal expected = totalBefore.subtract(bundleDiscount);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle bundle with multiple quantities per product")
    void shouldHandleBundleWithMultipleQuantities() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 2);
        bundleProducts.put(toothpaste, 3);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Family Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(2));
        cart.addItemQuantity(toothpaste, bd(3));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal bundlePrice = bd(2).multiply(bd(0.99))
                .add(bd(3).multiply(bd(1.79)));
        BigDecimal discountAmount = bundlePrice.multiply(bd(0.1))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expected = bundlePrice.subtract(discountAmount);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should handle custom bundle discount percentage")
    void shouldHandleCustomBundleDiscountPercentage() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = new ProductBundle(
                "Special Bundle", bundleProducts, bd(20.0));

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(0.99).add(bd(1.79))
                .multiply(bd(0.8));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }
}

