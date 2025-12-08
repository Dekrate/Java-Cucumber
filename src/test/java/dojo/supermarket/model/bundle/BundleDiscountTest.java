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
        assertTrue(receipt.getDiscounts().getFirst().description()
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

    @Test
    @DisplayName("ProductBundle should have correct getName")
    void productBundleShouldHaveCorrectGetName() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Test Bundle", bundleProducts);
        assertEquals("Test Bundle", bundle.name());
    }

    @Test
    @DisplayName("ProductBundle should have correct getRequiredProducts")
    void productBundleShouldHaveCorrectGetRequiredProducts() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 2);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Test Bundle", bundleProducts);
        assertEquals(bundleProducts, bundle.requiredProducts());
    }

    @Test
    @DisplayName("ProductBundle should have correct getDiscountPercentage")
    void productBundleShouldHaveCorrectGetDiscountPercentage() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        ProductBundle bundle = new ProductBundle(
                "Test Bundle", bundleProducts, bd(15.0));
        assertBigDecimalEquals(bd(15.0), bundle.discountPercentage());
    }

    @Test
    @DisplayName("ProductBundle should implement equals correctly")
    void productBundleShouldImplementEqualsCorrectly() {
        Map<Product, Integer> bundleProducts1 = new HashMap<>();
        bundleProducts1.put(toothbrush, 1);
        bundleProducts1.put(toothpaste, 1);

        Map<Product, Integer> bundleProducts2 = new HashMap<>();
        bundleProducts2.put(toothbrush, 1);
        bundleProducts2.put(toothpaste, 1);

        Map<Product, Integer> bundleProducts3 = new HashMap<>();
        bundleProducts3.put(toothbrush, 2);

        ProductBundle bundle1 = new ProductBundle(
                "Bundle A", bundleProducts1, bd(10.0));
        ProductBundle bundle2 = new ProductBundle(
                "Bundle A", bundleProducts2, bd(10.0));
        ProductBundle bundle3 = new ProductBundle(
                "Bundle B", bundleProducts1, bd(10.0));
        ProductBundle bundle4 = new ProductBundle(
                "Bundle A", bundleProducts3, bd(10.0));
        ProductBundle bundle5 = new ProductBundle(
                "Bundle A", bundleProducts1, bd(15.0));

        assertEquals(bundle1, bundle2);
        assertNotEquals(bundle1, bundle3);
        assertNotEquals(bundle1, bundle4);
        assertNotEquals(bundle1, bundle5);
        assertNotEquals(null, bundle1);
    }

    @Test
    @DisplayName("ProductBundle should implement hashCode correctly")
    void productBundleShouldImplementHashCodeCorrectly() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);

        ProductBundle bundle1 = new ProductBundle(
                "Bundle A", bundleProducts, bd(10.0));
        ProductBundle bundle2 = new ProductBundle(
                "Bundle A", bundleProducts, bd(10.0));

        assertEquals(bundle1.hashCode(), bundle2.hashCode());
    }

    @Test
    @DisplayName("ProductBundle should implement toString correctly")
    void productBundleShouldImplementToStringCorrectly() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);

        ProductBundle bundle = new ProductBundle(
                "Test Bundle", bundleProducts, bd(10.0));
        String str = bundle.toString();

        assertTrue(str.contains("Test Bundle"));
    }

    @Test
    @DisplayName("Should throw exception for empty bundle products")
    void shouldThrowExceptionForEmptyBundleProducts() {
        final Map<Product, Integer> emptyBundleProducts = new HashMap<>();
        final String bundleName = "Empty Bundle";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> ProductBundle.withDefaultDiscount(bundleName, emptyBundleProducts));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for negative discount percentage")
    void shouldThrowExceptionForNegativeDiscountPercentage() {
        final Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        final BigDecimal negativeDiscount = bd(-5.0);
        final String bundleName = "Test Bundle";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ProductBundle(bundleName, bundleProducts, negativeDiscount));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Should throw exception for discount percentage over 100")
    void shouldThrowExceptionForDiscountPercentageOver100() {
        final Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        final BigDecimal excessiveDiscount = bd(150.0);
        final String bundleName = "Test Bundle";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ProductBundle(bundleName, bundleProducts, excessiveDiscount));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("BundleDiscountCalculator should return null when no products in cart match bundle")
    void bundleDiscountCalculatorShouldReturnNullWhenNoProductsMatch() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(5).multiply(bd(1.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("BundleDiscountCalculator should handle when one product is missing from cart")
    void bundleDiscountCalculatorShouldHandleWhenOneProductMissing() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        bundleProducts.put(apples, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Mega Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(2));
        cart.addItemQuantity(toothpaste, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(0.99))
                .add(bd(2).multiply(bd(1.79)));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("BundleDiscountCalculator should handle zero quantity in cart")
    void bundleDiscountCalculatorShouldHandleZeroQuantityInCart() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        BundleDiscountCalculator calculator = new BundleDiscountCalculator();
        Map<Product, BigDecimal> productQuantities = new HashMap<>();
        productQuantities.put(toothbrush, BigDecimal.ZERO);
        productQuantities.put(toothpaste, bd(1));

        Discount discount = calculator.calculateBundleDiscount(
                bundle, productQuantities, catalog);

        assertNull(discount);
    }

    @Test
    @DisplayName("BundleDiscountCalculator should calculate correct discount description")
    void bundleDiscountCalculatorShouldCalculateCorrectDiscountDescription() {
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

        assertEquals(1, receipt.getDiscounts().size());
        String description = receipt.getDiscounts().getFirst().description();
        assertTrue(description.contains("Dental Care Bundle"));
        assertTrue(description.contains("10"));
    }

    @Test
    @DisplayName("BundleDiscountCalculator should handle large quantities")
    void bundleDiscountCalculatorShouldHandleLargeQuantities() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Dental Care Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(10));
        cart.addItemQuantity(toothpaste, bd(10));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal totalBeforeDiscount = bd(10).multiply(bd(0.99))
                .add(bd(10).multiply(bd(1.79)));
        BigDecimal bundlePrice = bd(0.99).add(bd(1.79));
        BigDecimal discountPerBundle = bundlePrice.multiply(bd(0.1))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDiscount = discountPerBundle.multiply(bd(10));
        BigDecimal expected = totalBeforeDiscount.subtract(totalDiscount);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle bundle with uneven product quantities in cart")
    void shouldHandleBundleWithUnevenProductQuantitiesInCart() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 2);
        bundleProducts.put(toothpaste, 3);
        ProductBundle bundle = ProductBundle.withDefaultDiscount(
                "Family Bundle", bundleProducts);

        teller.addProductBundle(bundle);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(4));
        cart.addItemQuantity(toothpaste, bd(5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal totalBeforeDiscount = bd(4).multiply(bd(0.99))
                .add(bd(5).multiply(bd(1.79)));
        BigDecimal bundlePrice = bd(2).multiply(bd(0.99))
                .add(bd(3).multiply(bd(1.79)));
        BigDecimal discountPerBundle = bundlePrice.multiply(bd(0.1))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expected = totalBeforeDiscount.subtract(discountPerBundle);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }
}

