package com.gildedrose.discount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages discount bundles in the Gilded Rose system.
 */
public class BundleManager {
    private List<DiscountBundle> bundles;

    public BundleManager() {
        this.bundles = new ArrayList<>();
    }

    public void addBundle(DiscountBundle bundle) {
        bundles.add(bundle);
    }

    public void removeBundle(String bundleName) {
        bundles.removeIf(bundle -> bundle.getBundleName().equals(bundleName));
    }

    public Optional<DiscountBundle> getBundle(String bundleName) {
        return bundles.stream()
            .filter(bundle -> bundle.getBundleName().equals(bundleName))
            .findFirst();
    }

    public List<DiscountBundle> getAllBundles() {
        return new ArrayList<>(bundles);
    }

    /**
     * Calculates the best bundle discount for a customer.
     */
    public double calculateBestDiscount(double basePrice) {
        return bundles.stream()
            .mapToDouble(bundle -> basePrice * bundle.getDiscountPercentage() / 100.0)
            .max()
            .orElse(0.0);
    }
}

