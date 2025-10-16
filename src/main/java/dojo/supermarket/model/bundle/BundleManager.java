package dojo.supermarket.model.bundle;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.SupermarketCatalog;

import java.util.*;

/**
 * Manages product bundles and calculates bundle discounts.
 * Follows Open/Closed Principle - new bundles are added as data, not code changes.
 */
public class BundleManager {

    private final List<ProductBundle> bundles = new ArrayList<>();

    public void addBundle(ProductBundle bundle) {
        bundles.add(bundle);
    }

    public List<ProductBundle> getBundles() {
        return Collections.unmodifiableList(bundles);
    }

    /**
     * Calculates bundle discounts for products in the cart.
     */
    public List<Discount> calculateBundleDiscounts(Map<Product, Double> cartProducts,
                                                     SupermarketCatalog catalog) {
        List<Discount> discounts = new ArrayList<>();
        List<Product> productsInCart = new ArrayList<>(cartProducts.keySet());

        for (ProductBundle bundle : bundles) {
            if (bundle.isApplicable(productsInCart)) {
                double bundleTotal = 0.0;
                for (Product product : bundle.getProducts()) {
                    bundleTotal += catalog.getUnitPrice(product);
                }
                double discountAmount = bundleTotal * bundle.getDiscountPercentage() / 100.0;

                // Create a virtual product for the bundle discount
                Product bundleProduct = bundle.getProducts().get(0);
                discounts.add(new Discount(bundleProduct, bundle.getDescription(), -discountAmount));
            }
        }

        return discounts;
    }
}

