package dojo.supermarket.model.bundle;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BundleManagerTest {

    private BundleManager manager;
    private SupermarketCatalog catalog;

    @BeforeEach
    void setUp() {
        manager = new BundleManager();
        catalog = new FakeCatalog();
    }

    @Test
    @DisplayName("BundleManager should allow adding bundles")
    void testAddBundle() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        ProductBundle bundle = new ProductBundle("Test", Arrays.asList(p1), 10.0);

        manager.addBundle(bundle);

        assertEquals(1, manager.getBundles().size());
        assertTrue(manager.getBundles().contains(bundle));
    }

    @Test
    @DisplayName("BundleManager should calculate discounts correctly")
    void testCalculateBundleDiscounts() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        Product p2 = new Product("p2", ProductUnit.EACH);

        catalog.addProduct(p1, 10.00);
        catalog.addProduct(p2, 20.00);

        ProductBundle bundle = new ProductBundle("Test", Arrays.asList(p1, p2), 10.0);
        manager.addBundle(bundle);

        Map<Product, Double> cartProducts = new HashMap<>();
        cartProducts.put(p1, 1.0);
        cartProducts.put(p2, 1.0);

        List<Discount> discounts = manager.calculateBundleDiscounts(cartProducts, catalog);

        assertEquals(1, discounts.size());
        assertEquals(-3.00, discounts.get(0).getDiscountAmount(), 0.01);
    }

    @Test
    @DisplayName("BundleManager should return empty list when no bundles applicable")
    void testNoApplicableBundles() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        Product p2 = new Product("p2", ProductUnit.EACH);

        catalog.addProduct(p1, 10.00);

        ProductBundle bundle = new ProductBundle("Test", Arrays.asList(p1, p2), 10.0);
        manager.addBundle(bundle);

        Map<Product, Double> cartProducts = new HashMap<>();
        cartProducts.put(p1, 1.0);

        List<Discount> discounts = manager.calculateBundleDiscounts(cartProducts, catalog);

        assertEquals(0, discounts.size());
    }

    @Test
    @DisplayName("BundleManager should return unmodifiable list")
    void testGetBundlesReturnsUnmodifiable() {
        List<ProductBundle> bundles = manager.getBundles();

        assertThrows(UnsupportedOperationException.class, () -> {
            Product p = new Product("p", ProductUnit.EACH);
            bundles.add(new ProductBundle("Test", Arrays.asList(p), 10.0));
        });
    }
}

