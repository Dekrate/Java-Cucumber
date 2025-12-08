package dojo.supermarket.model.offer;

import dojo.supermarket.model.SpecialOfferType;

/**
 * Factory for creating discount strategy instances.
 */
public final class DiscountStrategyFactory {

    /**
     * Strategy for three for two offers.
     */
    private static final ThreeForTwoStrategy THREE_FOR_TWO_STRATEGY =
            new ThreeForTwoStrategy();

    /**
     * Strategy for percentage discount offers.
     */
    private static final PercentageDiscountStrategy
            PERCENTAGE_DISCOUNT_STRATEGY =
            new PercentageDiscountStrategy();

    /**
     * Strategy for two for amount offers.
     */
    private static final TwoForAmountStrategy TWO_FOR_AMOUNT_STRATEGY =
            new TwoForAmountStrategy();

    /**
     * Strategy for five for amount offers.
     */
    private static final FiveForAmountStrategy
            FIVE_FOR_AMOUNT_STRATEGY = new FiveForAmountStrategy();

    private DiscountStrategyFactory() {
    }

    /**
     * Creates appropriate strategy for given offer type.
     *
     * @param offerType the offer type
     * @return corresponding discount strategy
     */
    public static DiscountStrategy createStrategy(
            final SpecialOfferType offerType) {
        return switch (offerType) {
            case THREE_FOR_TWO -> THREE_FOR_TWO_STRATEGY;
            case TEN_PERCENT_DISCOUNT -> PERCENTAGE_DISCOUNT_STRATEGY;
            case TWO_FOR_AMOUNT -> TWO_FOR_AMOUNT_STRATEGY;
            case FIVE_FOR_AMOUNT -> FIVE_FOR_AMOUNT_STRATEGY;
        };
    }
}
