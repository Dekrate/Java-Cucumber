package dojo.supermarket.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Helper class for test utilities when working with BigDecimal.
 */
public final class TestHelper {

    private static final int SCALE = 2;

    private TestHelper() {
    }

    /**
     * Creates a BigDecimal from a double value.
     *
     * @param value the double value
     * @return BigDecimal representation
     */
    public static BigDecimal bd(final double value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Creates a BigDecimal from an int value.
     *
     * @param value the int value
     * @return BigDecimal representation
     */
    public static BigDecimal bd(final int value) {
        return BigDecimal.valueOf(value);
    }

    /**
     * Creates a BigDecimal from a String value.
     *
     * @param value the string value
     * @return BigDecimal representation
     */
    public static BigDecimal bd(final String value) {
        return new BigDecimal(value);
    }

    /**
     * Asserts that two BigDecimal values are equal with proper rounding.
     *
     * @param expected the expected value
     * @param actual   the actual value
     */
    public static void assertBigDecimalEquals(final BigDecimal expected,
                                              final BigDecimal actual) {
        BigDecimal expectedRounded = expected.setScale(SCALE,
                RoundingMode.HALF_EVEN);
        BigDecimal actualRounded = actual.setScale(SCALE,
                RoundingMode.HALF_EVEN);
        assertEquals(0, expectedRounded.compareTo(actualRounded),
                "Expected " + expectedRounded + " but got " + actualRounded);
    }
}

