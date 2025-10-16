package dojo.supermarket.model.offer;

import dojo.supermarket.model.SpecialOfferType;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating offer strategies based on offer type.
 * This design follows the Open/Closed Principle - new offer types can be registered
 * without modifying the factory's core logic.
 */
public class OfferStrategyFactory {

    private static final Map<SpecialOfferType, OfferStrategy> strategies = new HashMap<>();

    static {
        // Register default strategies
        strategies.put(SpecialOfferType.THREE_FOR_TWO, new ThreeForTwoStrategy());
        strategies.put(SpecialOfferType.TWO_FOR_AMOUNT, new TwoForAmountStrategy());
        strategies.put(SpecialOfferType.FIVE_FOR_AMOUNT, new FiveForAmountStrategy());
        strategies.put(SpecialOfferType.TEN_PERCENT_DISCOUNT, new PercentageDiscountStrategy());
    }

    /**
     * Registers a new offer strategy.
     * Allows extending the system with new offer types without modifying existing code.
     */
    public static void registerStrategy(SpecialOfferType type, OfferStrategy strategy) {
        strategies.put(type, strategy);
    }

    /**
     * Gets the strategy for a given offer type.
     */
    public static OfferStrategy getStrategy(SpecialOfferType type) {
        OfferStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy registered for offer type: " + type);
        }
        return strategy;
    }
}

