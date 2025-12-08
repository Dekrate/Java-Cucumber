package dojo.supermarket.model.bundle;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
        catalog = new dojo.supermarket.model.FakeCatalog();
        teller = new Teller(catalog);

        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);

        catalog.addProduct(toothbrush, 0.99);
        catalog.addProduct(toothpaste, 1.79);
        catalog.addProduct(apples, 1.99);
    }

    @Test
    @DisplayName("Should apply bundle discount when all products are purchased")
    void shouldApplyBundleDiscountWhenComplete() {
        // Create bundle: toothbrush + toothpaste
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);
        cart.addItemQuantity(toothpaste, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Bundle price: 0.99 + 1.79 = 2.78
        // Discount: 10% of 2.78 = 0.278
        // Total: 2.78 - 0.278 = 2.502
        assertEquals(2.502, receipt.getTotalPrice(), 0.01);

        // Should have one bundle discount
        assertEquals(1, receipt.getDiscounts().size());
        assertTrue(receipt.getDiscounts().getFirst().description().contains("Dental Care Bundle"));
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
        cart.addItemQuantity(toothbrush, 1);
        // Missing toothpaste

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
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
        cart.addItemQuantity(toothbrush, 2);
        cart.addItemQuantity(toothpaste, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 2 complete bundles
        // Bundle price: 2.78 each
        // Discount per bundle: 0.278
        // Total discount: 0.556
        // Total: 5.56 - 0.556 = 5.004
        assertEquals(5.004, receipt.getTotalPrice(), 0.01);
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
        cart.addItemQuantity(toothbrush, 3);
        cart.addItemQuantity(toothpaste, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 2 complete bundles (limited by toothpaste quantity)
        // Bundle discount for 2 bundles: 2 * 0.278 = 0.556
        // Total before discount: 3*0.99 + 2*1.79 = 2.97 + 3.58 = 6.55
        // Total after discount: 6.55 - 0.556 = 5.994
        assertEquals(5.994, receipt.getTotalPrice(), 0.01);
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
        cart.addItemQuantity(toothbrush, 2);
        cart.addItemQuantity(toothpaste, 3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Bundle price: 2*0.99 + 3*1.79 = 1.98 + 5.37 = 7.35
        // Discount: 10% of 7.35 = 0.735
        // Total: 7.35 - 0.735 = 6.615
        assertEquals(6.615, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should combine bundle discount with regular offers")
    void shouldCombineBundleDiscountWithRegularOffers() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1);
        cart.addItemQuantity(toothpaste, 1);
        cart.addItemQuantity(apples, 2.0);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Bundle: 2.78 - 0.278 = 2.502
        // Apples: 2 * 1.99 = 3.98, with 20% off = 3.98 * 0.8 = 3.184
        // Total: 2.502 + 3.184 = 5.686
        assertEquals(5.686, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }
}

