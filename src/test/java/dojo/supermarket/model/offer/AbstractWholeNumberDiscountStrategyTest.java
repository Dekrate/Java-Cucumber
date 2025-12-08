package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class AbstractWholeNumberDiscountStrategyTest {

    private TestStrategy strategy;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        strategy = new TestStrategy();
        testProduct = new Product("Test Product", ProductUnit.EACH);
    }

    private BigDecimal bd(final double value) {
        return new BigDecimal(String.valueOf(value));
    }

    private void assertBigDecimalEquals(final BigDecimal expected,
                                        final BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual),
                "Expected " + expected + " but was " + actual);
    }

    @Test
    @DisplayName("isWholeNumber should return true for whole numbers")
    void isWholeNumberShouldReturnTrueForWholeNumbers() {
        assertTrue(strategy.testIsWholeNumber(bd(5.0)));
        assertTrue(strategy.testIsWholeNumber(bd(10)));
        assertTrue(strategy.testIsWholeNumber(BigDecimal.ZERO));
        assertTrue(strategy.testIsWholeNumber(bd(100.00)));
    }

    @Test
    @DisplayName("isWholeNumber should return false for decimal numbers")
    void isWholeNumberShouldReturnFalseForDecimalNumbers() {
        assertFalse(strategy.testIsWholeNumber(bd(5.5)));
        assertFalse(strategy.testIsWholeNumber(bd(10.1)));
        assertFalse(strategy.testIsWholeNumber(bd(0.5)));
        assertFalse(strategy.testIsWholeNumber(bd(99.99)));
    }

    @Test
    @DisplayName("validateAndConvertQuantity should return empty for non-whole numbers")
    void validateAndConvertQuantityShouldReturnEmptyForNonWholeNumbers() {
        final var result = strategy.testValidateAndConvertQuantity(bd(5.5), 3);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("validateAndConvertQuantity should return empty when below minimum")
    void validateAndConvertQuantityShouldReturnEmptyWhenBelowMinimum() {
        final var result = strategy.testValidateAndConvertQuantity(bd(2), 3);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("validateAndConvertQuantity should return value when valid")
    void validateAndConvertQuantityShouldReturnValueWhenValid() {
        final var result = strategy.testValidateAndConvertQuantity(bd(5), 3);
        assertTrue(result.isPresent());
        assertEquals(5, result.getAsInt());
    }

    @Test
    @DisplayName("validateAndConvertQuantity should return value when exactly at minimum")
    void validateAndConvertQuantityShouldReturnValueWhenExactlyAtMinimum() {
        final var result = strategy.testValidateAndConvertQuantity(bd(3), 3);
        assertTrue(result.isPresent());
        assertEquals(3, result.getAsInt());
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should return null for non-whole quantity")
    void calculateSetBasedDiscountShouldReturnNullForNonWholeQuantity() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, price) -> price.multiply(BigDecimal.valueOf(sets * 2));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(5.5),
                bd(10.0),
                3,
                calculator,
                "Test Discount"
        );

        assertNull(discount);
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should return null when quantity below minimum")
    void calculateSetBasedDiscountShouldReturnNullWhenQuantityBelowMinimum() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, price) -> price.multiply(BigDecimal.valueOf(sets * 2));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(2),
                bd(10.0),
                3,
                calculator,
                "Test Discount"
        );

        assertNull(discount);
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should calculate discount correctly for exact sets")
    void calculateSetBasedDiscountShouldCalculateDiscountCorrectlyForExactSets() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, _) -> bd(15.0).multiply(BigDecimal.valueOf(sets));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(6),
                bd(10.0),
                3,
                calculator,
                "2 for 15"
        );

        assertNotNull(discount);
        assertEquals("2 for 15", discount.description());
        assertEquals(testProduct, discount.product());
        assertBigDecimalEquals(bd(-30.0), discount.discountAmount());
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should handle remainder items correctly")
    void calculateSetBasedDiscountShouldHandleRemainderItemsCorrectly() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, _) -> bd(15.0).multiply(BigDecimal.valueOf(sets));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(7),
                bd(10.0),
                3,
                calculator,
                "2 for 15"
        );

        assertNotNull(discount);
        assertBigDecimalEquals(bd(-30.0), discount.discountAmount());
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should work with unit price in calculator")
    void calculateSetBasedDiscountShouldWorkWithUnitPriceInCalculator() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, price) -> price.multiply(BigDecimal.valueOf(sets * 2));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(6),
                bd(10.0),
                3,
                calculator,
                "3 for 2"
        );

        assertNotNull(discount);
        assertBigDecimalEquals(bd(-20.0), discount.discountAmount());
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should handle single set")
    void calculateSetBasedDiscountShouldHandleSingleSet() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, price) -> price.multiply(BigDecimal.valueOf(sets * 2));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(3),
                bd(10.0),
                3,
                calculator,
                "3 for 2"
        );

        assertNotNull(discount);
        assertEquals(testProduct, discount.product());
        assertBigDecimalEquals(bd(-10.0), discount.discountAmount());
    }

    @Test
    @DisplayName("calculateSetBasedDiscount should handle large quantities")
    void calculateSetBasedDiscountShouldHandleLargeQuantities() {
        final BiFunction<Integer, BigDecimal, BigDecimal> calculator =
                (sets, price) -> price.multiply(BigDecimal.valueOf(sets * 4));

        final Discount discount = strategy.testCalculateSetBasedDiscount(
                testProduct,
                bd(100),
                bd(1.0),
                5,
                calculator,
                "5 for 4"
        );

        assertNotNull(discount);
        assertBigDecimalEquals(bd(-20.0), discount.discountAmount());
    }

    private static class TestStrategy extends AbstractWholeNumberDiscountStrategy {

        @Override
        public Discount calculateDiscount(final Product product,
                                          final BigDecimal quantity,
                                          final BigDecimal unitPrice,
                                          final BigDecimal argument) {
            return null;
        }

        public boolean testIsWholeNumber(final BigDecimal value) {
            return isWholeNumber(value);
        }

        public java.util.OptionalInt testValidateAndConvertQuantity(
                final BigDecimal quantity,
                final int minimumRequired) {
            return validateAndConvertQuantity(quantity, minimumRequired);
        }

        public Discount testCalculateSetBasedDiscount(
                final Product product,
                final BigDecimal quantity,
                final BigDecimal unitPrice,
                final int itemsInSet,
                final BiFunction<Integer, BigDecimal, BigDecimal> setPriceCalculator,
                final String description) {
            return calculateSetBasedDiscount(product, quantity, unitPrice,
                    itemsInSet, setPriceCalculator, description);
        }
    }
}

