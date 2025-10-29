# Supermarket Checkout System - Open/Closed Principle Implementation

## Overview

This project demonstrates a refactored supermarket checkout system that adheres to the **Open/Closed Principle** (OCP). The system is designed to be open for extension but closed for modification, allowing new features to be added without changing existing code.

## Open/Closed Principle Implementation

### Problem Statement

The original system contained a large `if-else` chain in the `ShoppingCart.handleOffers()` method that violated the Open/Closed Principle. Adding new offer types required modifying existing code, making the system fragile and difficult to maintain.

### Solution Architecture

The refactored system implements OCP through several design patterns:

## 1. Product Categories System

### Design

The product category system uses the **Strategy Pattern** to enable different product types with their own business rules.

#### Interface: `ProductCategory`

Defines the contract for product categories, allowing new categories to be added without modifying existing code.

```java
public interface ProductCategory {
    String getCategoryName();
    double applyPriceAdjustment(double basePrice, double quantity);
    boolean hasBundleRules();
}
```

#### Implementations

- **StandardCategory**: Default category with no special rules
- **ConjuredCategory**: Items that degrade twice as fast as normal items
- **PremiumCategory**: High-quality items with potential special handling

### Extensibility

New product categories can be added by implementing the `ProductCategory` interface:

```java
public class OrganicCategory implements ProductCategory {
    @Override
    public String getCategoryName() { return "Organic"; }
    
    @Override
    public double applyPriceAdjustment(double basePrice, double quantity) {
        return basePrice * quantity * 1.15; // 15% premium
    }
}
```

### How It Satisfies OCP

- **Open for extension**: New categories can be added by creating new implementations
- **Closed for modification**: Existing category classes and the Product class remain unchanged
- Products can be assigned different categories at runtime using `product.setCategory()`

## 2. Special Offers System

### Design

The special offers system uses the **Strategy Pattern** combined with a **Factory Pattern** to eliminate the `if-else` chain.

#### Interface: `OfferStrategy`

Each offer type implements this interface with its own discount calculation logic.

```java
public interface OfferStrategy {
    Discount calculateDiscount(Product product, double quantity, 
                              double unitPrice, double argument);
    String getDescription();
}
```

#### Implementations

- **ThreeForTwoStrategy**: Buy 3, pay for 2
- **TwoForAmountStrategy**: Buy 2 for a fixed amount
- **FiveForAmountStrategy**: Buy 5 for a fixed amount
- **PercentageDiscountStrategy**: Apply percentage discount

#### Factory: `OfferStrategyFactory`

Maps offer types to their corresponding strategies:

```java
public class OfferStrategyFactory {
    private static final Map<SpecialOfferType, OfferStrategy> strategies;
    
    public static void registerStrategy(SpecialOfferType type, OfferStrategy strategy) {
        strategies.put(type, strategy);
    }
    
    public static OfferStrategy getStrategy(SpecialOfferType type) {
        return strategies.get(type);
    }
}
```

### Refactored Code

**Before (violates OCP):**
```java
if (offer.offerType == SpecialOfferType.THREE_FOR_TWO) {
    // calculation logic
} else if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT) {
    // calculation logic
} else if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT) {
    // calculation logic
}
// ... more conditions
```

**After (follows OCP):**
```java
OfferStrategy strategy = OfferStrategyFactory.getStrategy(offer.offerType);
Discount discount = strategy.calculateDiscount(p, quantity, unitPrice, offer.argument);
```

### Extensibility

New offer types can be added in three ways:

1. **Create new strategy implementation:**
```java
public class BuyOneGetOneFreeStrategy implements OfferStrategy {
    @Override
    public Discount calculateDiscount(...) {
        // Implementation
    }
}
```

2. **Register with factory:**
```java
OfferStrategyFactory.registerStrategy(
    SpecialOfferType.BUY_ONE_GET_ONE_FREE, 
    new BuyOneGetOneFreeStrategy()
);
```

3. **No modification of existing offer strategies required**

### How It Satisfies OCP

- **Open for extension**: New offer strategies can be added by implementing `OfferStrategy`
- **Closed for modification**: The `ShoppingCart.handleOffers()` method never needs to change
- The factory allows runtime registration of new strategies

## 3. Discounted Bundles System

### Design

The bundle system uses **data-driven design** where bundles are added as data rather than code.

#### Class: `ProductBundle`

Represents a collection of products sold together at a discount:

```java
public class ProductBundle {
    private final String name;
    private final List<Product> products;
    private final double discountPercentage;
    
    public boolean isApplicable(List<Product> cartProducts) {
        return cartProducts.containsAll(products);
    }
}
```

#### Manager: `BundleManager`

Manages bundle registration and discount calculation:

```java
public class BundleManager {
    private final List<ProductBundle> bundles;
    
    public void addBundle(ProductBundle bundle) {
        bundles.add(bundle);
    }
    
    public List<Discount> calculateBundleDiscounts(...) {
        // Calculates discounts for applicable bundles
    }
}
```

### Usage Example

```java
ProductBundle breakfastBundle = new ProductBundle(
    "Breakfast Special",
    Arrays.asList(bread, butter, jam),
    15.0  // 15% discount
);
teller.getBundleManager().addBundle(breakfastBundle);
```

### Extensibility

New bundles are added as data without code changes:

```java
// Add new bundle - no code modification needed
ProductBundle lunchBundle = new ProductBundle(
    "Lunch Combo",
    Arrays.asList(sandwich, chips, drink),
    20.0
);
bundleManager.addBundle(lunchBundle);
```

### How It Satisfies OCP

- **Open for extension**: New bundles can be added at runtime
- **Closed for modification**: Bundle calculation logic in `BundleManager` remains unchanged
- Bundles are defined declaratively as data, not procedurally as code

## 4. Loyalty Programmes System

### Design

The loyalty programme system uses the **Strategy Pattern** with a **Chain of Responsibility** approach.

#### Interface: `LoyaltyProgram`

Defines loyalty tier behavior:

```java
public interface LoyaltyProgram {
    String getTierName();
    double getDiscountPercentage();
    double getPointsMultiplier();
    boolean isApplicable(double totalAmount);
}
```

#### Implementations

- **BasicLoyaltyTier**: No discount, 1x points (default)
- **SilverLoyaltyTier**: 5% discount, 1.5x points (purchases ≥ $20)
- **GoldLoyaltyTier**: 10% discount, 2x points (purchases ≥ $50)

#### Manager: `LoyaltyProgramManager`

Determines applicable tier and calculates loyalty discounts:

```java
public class LoyaltyProgramManager {
    private final List<LoyaltyProgram> programs;
    
    public LoyaltyProgram getApplicableTier(double totalAmount) {
        for (LoyaltyProgram program : programs) {
            if (program.isApplicable(totalAmount)) {
                return program;
            }
        }
        return new BasicLoyaltyTier();
    }
    
    public Discount calculateLoyaltyDiscount(...) {
        // Calculates discount based on tier
    }
}
```

### Extensibility

New loyalty tiers can be added without modifying existing tiers:

```java
public class PlatinumLoyaltyTier implements LoyaltyProgram {
    @Override
    public String getTierName() { return "Platinum"; }
    
    @Override
    public double getDiscountPercentage() { return 15.0; }
    
    @Override
    public double getPointsMultiplier() { return 3.0; }
    
    @Override
    public boolean isApplicable(double totalAmount) {
        return totalAmount >= 100.0;
    }
}

// Register new tier
loyaltyManager.addLoyaltyProgram(new PlatinumLoyaltyTier());
```

### How It Satisfies OCP

- **Open for extension**: New loyalty tiers can be added by implementing `LoyaltyProgram`
- **Closed for modification**: `LoyaltyProgramManager` logic remains unchanged
- Tier determination uses polymorphism instead of conditional logic

## Architectural Analysis and Alternative Solutions

The selection of the **Strategy Pattern** for the special offers system was a deliberate architectural decision. This section explores the rationale behind this choice, compares it with other potential patterns, and discusses the trade-offs involved.

### Why the Strategy Pattern?

The primary goal was to replace the rigid `if-else` structure in `ShoppingCart.handleOffers()` with a flexible and extensible solution. The Strategy Pattern was chosen because it perfectly aligns with the **Open/Closed Principle** in this context:

- **Encapsulation of Algorithms**: Each offer type (e.g., "3 for 2", "10% discount") is an independent algorithm. The Strategy Pattern encapsulates each algorithm into its own class (`ThreeForTwoStrategy`, `PercentageDiscountStrategy`).
- **Single Responsibility Principle (SRP)**: Each strategy class has only one reason to change: a modification to its specific discount calculation logic. This prevents a single monolithic class from becoming bloated with unrelated responsibilities.
- **Simplicity and Clarity**: The client code (`ShoppingCart`) becomes much simpler. It no longer needs to know the details of each offer. It simply obtains the correct strategy from a factory and executes it. This delegation of responsibility makes the code easier to read and maintain.

### Alternative 1: Visitor Pattern

The Visitor Pattern is another behavioral pattern that can be used to add new operations to an object structure without modifying the objects themselves.

- **How it would work**: We could have a `DiscountVisitor` interface with methods like `visit(ThreeForTwoOffer offer)`, `visit(PercentageDiscountOffer offer)`, etc. The `Offer` objects would have an `accept(DiscountVisitor visitor)` method.
- **Why it was not chosen**:
    - **Violation of OCP for New Offers**: The core issue with the Visitor pattern here is that the `Visitor` interface must be modified every time a new offer type (a new `Visitable` class) is added. For example, adding a `BuyOneGetOneFreeOffer` would require adding a new `visit(BuyOneGetOneFreeOffer offer)` method to the `DiscountVisitor` interface and all its implementations. This violates the Open/Closed Principle, which was the primary problem we aimed to solve.
    - **Complexity**: While powerful, the Visitor pattern introduces a "double dispatch" mechanism that can be more complex to understand and implement compared to the straightforward nature of the Strategy pattern in this scenario.

The Strategy Pattern is superior here because adding a new offer type only requires creating a new strategy class and registering it with the factory—no existing code needs to be modified.

### Alternative 2: Chain of Responsibility Pattern

This pattern could be used to create a chain of offer-handling objects. Each handler would check if it could apply its discount and, if not, pass the request to the next handler in the chain.

- **How it would work**: We could create a chain of `OfferHandler` objects. The `ShoppingCart` would pass the product and quantity to the head of the chain.
- **Why it was not chosen**:
    - **Not a Natural Fit for This Problem**: The Chain of Responsibility pattern excels when there is a sequence of potential handlers for a single request, and only one handler is expected to process it (or the chain is processed in a specific order). In our case, a product has one specific offer type associated with it (`Offer.offerType`). We don't need to "discover" which offer applies; we already know. We just need to execute the correct algorithm.
    - **Unnecessary Overhead**: Using a chain introduces complexity (managing the chain, passing requests) that is not required here. A direct lookup in a factory (as used with the Strategy pattern) is far more efficient and direct. The `Map` in `OfferStrategyFactory` provides an O(1) lookup, which is more performant than traversing a chain.

### Trade-offs of the Chosen Approach (Strategy Pattern)

While the Strategy Pattern provides significant benefits, it's important to acknowledge its trade-offs:

- **Increased Number of Classes**: The most noticeable trade-off is the proliferation of classes. Each new offer type requires a new class. In a system with dozens of offers, this can lead to a large number of small files. However, this is a manageable complexity, and modern IDEs make navigating between these classes easy. The benefit of clear separation of concerns far outweighs the cost of having more classes.
- **Indirection via Factory**: The introduction of the `OfferStrategyFactory` adds a layer of indirection. While this is what enables the decoupling and adherence to OCP, it means a developer needs to understand that the strategies are registered and retrieved from the factory. This is a minor learning curve compared to deciphering a complex `if-else` block.

## System Integration

All components work together in the `Teller` class:

```java
public Receipt checksOutArticlesFrom(ShoppingCart cart) {
    Receipt receipt = new Receipt();
    
    // 1. Add products with base prices
    // 2. Apply special offers (Strategy Pattern)
    cart.handleOffers(receipt, offers, catalog);
    
    // 3. Apply bundle discounts (Data-driven)
    List<Discount> bundleDiscounts = bundleManager.calculateBundleDiscounts(...);
    
    // 4. Apply loyalty discounts (Strategy Pattern)
    Discount loyaltyDiscount = loyaltyManager.calculateLoyaltyDiscount(...);
    
    return receipt;
}
```

## Benefits of This Architecture

### 1. Extensibility
- New product categories, offer types, bundles, and loyalty tiers can be added without modifying existing code
- Extensions are made through new classes implementing existing interfaces

### 2. Maintainability
- Each discount type has its own class with single responsibility
- No large `if-else` or `switch` statements
- Changes to one offer type do not affect others

### 3. Testability
- Each strategy can be tested independently
- Easy to mock and inject dependencies
- Comprehensive test coverage achieved (see test files)

### 4. Flexibility
- Strategies can be registered at runtime
- Bundles and loyalty tiers are data-driven
- Product categories can be changed dynamically

## Test Coverage

The system includes comprehensive tests covering:

- **Category Tests**: All product category implementations
- **Offer Strategy Tests**: Each offer strategy with edge cases
- **Bundle Tests**: Bundle creation, applicability, and discount calculation
- **Loyalty Tests**: All loyalty tiers and manager functionality
- **Integration Tests**: Complete checkout scenarios
- **Edge Cases**: Empty carts, large quantities, fractional amounts

Run tests with:
```bash
mvn test
```

## Adding New Features (Examples)

### Adding a New Product Category

```java
// 1. Create new category
public class PerishableCategory implements ProductCategory {
    @Override
    public String getCategoryName() { return "Perishable"; }
    
    @Override
    public double applyPriceAdjustment(double basePrice, double quantity) {
        // Apply special logic for perishable items
        return basePrice * quantity;
    }
}

// 2. Use it
Product milk = new Product("milk", ProductUnit.EACH, new PerishableCategory());
```

### Adding a New Offer Type

```java
// 1. Add enum value
public enum SpecialOfferType {
    // existing types...
    BUY_X_GET_Y_FREE
}

// 2. Create strategy
public class BuyXGetYFreeStrategy implements OfferStrategy {
    @Override
    public Discount calculateDiscount(...) {
        // Implementation
    }
}

// 3. Register strategy
OfferStrategyFactory.registerStrategy(
    SpecialOfferType.BUY_X_GET_Y_FREE,
    new BuyXGetYFreeStrategy()
);
```

### Adding a New Bundle

```java
// No code changes needed - just data
ProductBundle dinnerBundle = new ProductBundle(
    "Dinner Pack",
    Arrays.asList(pasta, sauce, cheese),
    25.0
);
teller.getBundleManager().addBundle(dinnerBundle);
```

### Adding a New Loyalty Tier

```java
// 1. Create tier
public class DiamondLoyaltyTier implements LoyaltyProgram {
    @Override
    public String getTierName() { return "Diamond"; }
    
    @Override
    public double getDiscountPercentage() { return 20.0; }
    
    @Override
    public double getPointsMultiplier() { return 4.0; }
    
    @Override
    public boolean isApplicable(double totalAmount) {
        return totalAmount >= 200.0;
    }
}

// 2. Register tier
teller.getLoyaltyManager().addLoyaltyProgram(new DiamondLoyaltyTier());
```

## Summary

This refactored system demonstrates the Open/Closed Principle through:

1. **Strategy Pattern** for offers and loyalty programmes
2. **Factory Pattern** for offer strategy creation
3. **Data-driven design** for product bundles
4. **Interface-based design** for product categories

All new features can be added through extension rather than modification, making the system robust, maintainable, and easy to test.
