package dojo.supermarket.model.gildedrose.discount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages discount strategies in the Gilded Rose system.
 * Supports multiple discount types through DiscountStrategy interface.
 * Complies with Open/Closed Principle - open for extension, closed for modification.
 */
public class BundleManager {
    private List<DiscountStrategy> discountStrategies;

    public BundleManager() {
        this.discountStrategies = new ArrayList<>();
    }

    /**
     * Adds any discount strategy.
     * Allows extension with new discount types without modifying this class.
     */
    public void addDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.add(strategy);
    }

    public void removeDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.remove(strategy);
    }

    /**
     * Adds a bundle as a discount strategy.
     * Maintains backward compatibility.
     */
    public void addBundle(DiscountBundle bundle) {
        addDiscountStrategy(bundle);
    }

    /**
     * Removes a bundle by name.
     * Maintains backward compatibility.
     */
    public void removeBundle(String bundleName) {
        discountStrategies.removeIf(strategy ->
            strategy instanceof DiscountBundle &&
            ((DiscountBundle) strategy).getBundleName().equals(bundleName));
    }

    public Optional<DiscountBundle> getBundle(String bundleName) {
        return discountStrategies.stream()
            .filter(strategy -> strategy instanceof DiscountBundle)
            .map(strategy -> (DiscountBundle) strategy)
            .filter(bundle -> bundle.getBundleName().equals(bundleName))
            .findFirst();
    }

    public List<DiscountBundle> getAllBundles() {
        return discountStrategies.stream()
            .filter(strategy -> strategy instanceof DiscountBundle)
            .map(strategy -> (DiscountBundle) strategy)
            .collect(java.util.stream.Collectors.toList());
    }

    public List<DiscountStrategy> getAllDiscountStrategies() {
        return new ArrayList<>(discountStrategies);
    }

    /**
     * Calculates the best discount from all available strategies.
     */
    public double calculateBestDiscount(double basePrice) {
        return discountStrategies.stream()
            .mapToDouble(strategy -> strategy.calculateDiscount(basePrice))
            .max()
            .orElse(0.0);
    }

    /**
     * Gets the best discount strategy for a given price.
     */
    public Optional<DiscountStrategy> getBestDiscountStrategy(double basePrice) {
        return discountStrategies.stream()
            .max((s1, s2) -> Double.compare(
                s1.calculateDiscount(basePrice),
                s2.calculateDiscount(basePrice)
            ));
    }
}
