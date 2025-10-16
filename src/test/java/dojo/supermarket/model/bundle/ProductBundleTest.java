package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductBundleTest {

    @Test
    @DisplayName("ProductBundle should be created with correct properties")
    void testBundleCreation() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        Product p2 = new Product("p2", ProductUnit.EACH);

        ProductBundle bundle = new ProductBundle("Test Bundle", Arrays.asList(p1, p2), 20.0);

        assertEquals("Test Bundle", bundle.getName());
        assertEquals(2, bundle.getProducts().size());
        assertEquals(20.0, bundle.getDiscountPercentage());
        assertEquals("Test Bundle bundle - 20.0% off", bundle.getDescription());
    }

    @Test
    @DisplayName("Bundle should be applicable when all products present")
    void testBundleApplicability() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        Product p2 = new Product("p2", ProductUnit.EACH);
        Product p3 = new Product("p3", ProductUnit.EACH);

        ProductBundle bundle = new ProductBundle("Test", Arrays.asList(p1, p2), 10.0);

        List<Product> cartWithAll = Arrays.asList(p1, p2, p3);
        List<Product> cartMissing = Arrays.asList(p1, p3);

        assertTrue(bundle.isApplicable(cartWithAll));
        assertFalse(bundle.isApplicable(cartMissing));
    }

    @Test
    @DisplayName("Bundles should be equal based on name")
    void testBundleEquality() {
        Product p1 = new Product("p1", ProductUnit.EACH);

        ProductBundle bundle1 = new ProductBundle("Same", Arrays.asList(p1), 10.0);
        ProductBundle bundle2 = new ProductBundle("Same", Arrays.asList(p1), 20.0);
        ProductBundle bundle3 = new ProductBundle("Different", Arrays.asList(p1), 10.0);

        assertEquals(bundle1, bundle2);
        assertNotEquals(bundle1, bundle3);
        assertEquals(bundle1.hashCode(), bundle2.hashCode());
    }

    @Test
    @DisplayName("Bundle equals should handle null and different types")
    void testBundleEqualsEdgeCases() {
        Product p1 = new Product("p1", ProductUnit.EACH);
        ProductBundle bundle = new ProductBundle("Test", Arrays.asList(p1), 10.0);

        assertTrue(bundle.equals(bundle));
        assertFalse(bundle.equals(null));
        assertFalse(bundle.equals("Not a bundle"));
    }
}

