package dojo.supermarket.model;

import dojo.supermarket.model.offer.DiscountStrategy;
import dojo.supermarket.model.offer.DiscountStrategyFactory;

import java.math.BigDecimal;

/**
 * Represents a special offer on a product.
 */
public final class Offer {

    /** The type of special offer. */
    private final SpecialOfferType offerType;
    /** The product this offer applies to. */
    private final Product product;
    /** The argument for the offer calculation. */
    private final BigDecimal argument;
    /** The discount strategy to use. */
    private final DiscountStrategy strategy;

    /**
     * Creates a new offer.
     *
     * @param type     the type of offer
     * @param prod     the product
     * @param arg      the offer argument
     */
    public Offer(final SpecialOfferType type,
                 final Product prod,
                 final BigDecimal arg) {
        this.offerType = type;
        this.argument = arg;
        this.product = prod;
        this.strategy = DiscountStrategyFactory.createStrategy(type);
    }

    /**
     * Calculates discount for given quantity and price.
     *
     * @param quantity  the quantity
     * @param unitPrice the unit price
     * @return discount object
     */
    public Discount calculateDiscount(final BigDecimal quantity,
                                       final BigDecimal unitPrice) {
        return strategy.calculateDiscount(product, quantity,
                unitPrice, argument);
    }

    /**
     * Gets the offer type.
     *
     * @return the offer type
     */
    public SpecialOfferType getOfferType() {
        return offerType;
    }

    /**
     * Gets the product.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the argument.
     *
     * @return the offer argument
     */
    public BigDecimal getArgument() {
        return argument;
    }

    /**
     * Gets the strategy.
     *
     * @return the discount strategy
     */
    public DiscountStrategy getStrategy() {
        return strategy;
    }
}

