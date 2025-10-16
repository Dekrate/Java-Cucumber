package dojo.supermarket.model.offer;

import dojo.supermarket.model.SpecialOfferType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferStrategyFactoryTest {

    @Test
    @DisplayName("Factory should return correct strategy for THREE_FOR_TWO")
    void testGetThreeForTwoStrategy() {
        OfferStrategy strategy = OfferStrategyFactory.getStrategy(SpecialOfferType.THREE_FOR_TWO);

        assertNotNull(strategy);
        assertTrue(strategy instanceof ThreeForTwoStrategy);
    }

    @Test
    @DisplayName("Factory should return correct strategy for TWO_FOR_AMOUNT")
    void testGetTwoForAmountStrategy() {
        OfferStrategy strategy = OfferStrategyFactory.getStrategy(SpecialOfferType.TWO_FOR_AMOUNT);

        assertNotNull(strategy);
        assertTrue(strategy instanceof TwoForAmountStrategy);
    }

    @Test
    @DisplayName("Factory should return correct strategy for FIVE_FOR_AMOUNT")
    void testGetFiveForAmountStrategy() {
        OfferStrategy strategy = OfferStrategyFactory.getStrategy(SpecialOfferType.FIVE_FOR_AMOUNT);

        assertNotNull(strategy);
        assertTrue(strategy instanceof FiveForAmountStrategy);
    }

    @Test
    @DisplayName("Factory should return correct strategy for TEN_PERCENT_DISCOUNT")
    void testGetPercentageDiscountStrategy() {
        OfferStrategy strategy = OfferStrategyFactory.getStrategy(SpecialOfferType.TEN_PERCENT_DISCOUNT);

        assertNotNull(strategy);
        assertTrue(strategy instanceof PercentageDiscountStrategy);
    }

    @Test
    @DisplayName("Factory should allow custom strategy registration")
    void testRegisterCustomStrategy() {
        OfferStrategy customStrategy = new PercentageDiscountStrategy();

        assertDoesNotThrow(() ->
            OfferStrategyFactory.registerStrategy(SpecialOfferType.TEN_PERCENT_DISCOUNT, customStrategy)
        );
    }
}

