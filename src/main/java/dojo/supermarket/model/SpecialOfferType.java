package dojo.supermarket.model;

/**
 * Types of special offers available.
 */
public enum SpecialOfferType {
    /** Buy 3, pay for 2. */
    THREE_FOR_TWO,
    /** Percentage discount (10%, 20%, etc.). */
    TEN_PERCENT_DISCOUNT,
    /** Two items for a fixed amount. */
    TWO_FOR_AMOUNT,
    /** Five items for a fixed amount. */
    FIVE_FOR_AMOUNT
}
