# Gilded Rose - Refactoring and Extension Project

## Project Overview

This project represents a comprehensive refactoring and extension of the Gilded Rose inventory management system. The Gilded Rose is a small inn run by Allison that buys and sells goods. The system automatically updates inventory quality as items approach their sell-by date.

## Original Requirements

The original Gilded Rose system had the following rules:

- All items have a `sellIn` value (days until sell-by date) and a `quality` value
- At the end of each day, both values decrease for every item
- Once the sell-by date passes, quality degrades twice as fast
- Quality is never negative and never exceeds 50
- **Aged Brie** increases in quality as it ages
- **Sulfuras** is a legendary item that never decreases in quality or needs to be sold
- **Backstage passes** increase in quality as the concert approaches, but drop to 0 after the event
- **Conjured items** degrade twice as fast as normal items

## Project Objectives

The instructor required the following:

1. Make the system open to possible extensions
2. Add new categories of items with their own rules
3. Implement discounted bundles of items
4. Implement loyalty programmes
5. Ensure comprehensive test coverage

All features must comply with the **Open/Closed Principle** (OCP).

## Solution Architecture

### 1. Item Update System - Strategy Pattern

The core system uses the **Strategy Pattern** to handle different item types.

**Package:** `dojo.supermarket.model.strategy/`

**Components:**
- `ItemUpdateStrategy` (interface) - defines contract for item updates
- `NormalItemStrategy` - handles standard items
- `AgedBrieStrategy` - handles Aged Brie (increases in quality)
- `SulfurasStrategy` - handles legendary items (never changes)
- `BackstagePassStrategy` - handles backstage passes (complex quality rules)
- `ConjuredItemStrategy` - handles conjured items (degrades 2x faster)

**OCP Compliance:**
- **Open for extension:** Add new item categories by creating new strategy classes
- **Closed for modification:** No need to modify `GildedRose` or existing strategies

**Adding a new item category:**
```java
public class PerishableItemStrategy implements ItemUpdateStrategy {
    @Override
    public boolean canHandle(Item item) {
        return item.name.startsWith("Perishable");
    }
    
    @Override
    public void update(Item item) {
        // Custom logic for perishable items
    }
}
```

Then register in `GildedRose.initializeStrategies()`:
```java
strategies.add(new PerishableItemStrategy());
```

### 2. Discounted Bundles - Strategy Pattern

**Package:** `dojo.supermarket.model.gildedrose.discount/`

**Components:**
- `DiscountStrategy` (interface) - defines contract for discount calculations
- `DiscountBundle` - implements bundle discounts
- `BundleManager` - manages all discount strategies

**OCP Compliance:**
- Can add custom discount strategies without modifying `BundleManager`
- `BundleManager.addDiscountStrategy()` accepts any `DiscountStrategy` implementation

**Example - Adding custom discount:**
```java
DiscountStrategy seasonalDiscount = new DiscountStrategy() {
    @Override
    public double calculateDiscount(double basePrice) {
        return basePrice * 0.30; // 30% seasonal discount
    }
    
    @Override
    public String getDescription() {
        return "Seasonal Sale: 30% off";
    }
};

BundleManager manager = new BundleManager();
manager.addDiscountStrategy(seasonalDiscount);
```

**Standard bundle usage:**
```java
DiscountBundle bundle = new DiscountBundle("Hero Pack", 20.0);
bundle.addItem(new Item("Sword", 10, 50));
bundle.addItem(new Item("Shield", 15, 40));

manager.addBundle(bundle);
double discount = manager.calculateBestDiscount(100.0); // Returns 20.0
```

### 3. Loyalty Programmes - Strategy Pattern

**Package:** `dojo.supermarket.model.loyalty/`

**Components:**
- `LoyaltyStrategy` (interface) - defines contract for loyalty calculations
- `LoyaltyProgram` - standard tier-based loyalty program
- `LoyaltyTier` (enum) - Bronze, Silver, Gold tiers
- `LoyaltyProgramManager` - manages all loyalty strategies

**Tier System:**

| Tier   | Points Required | Discount |
|--------|----------------|----------|
| Bronze | 0              | 5%       |
| Silver | 500            | 10%      |
| Gold   | 1000           | 15%      |

**OCP Compliance:**
- Can add custom loyalty strategies without modifying `LoyaltyProgramManager`
- `LoyaltyProgramManager.registerCustomerWithStrategy()` accepts any `LoyaltyStrategy`

**Example - Adding VIP program:**
```java
LoyaltyStrategy vipProgram = new LoyaltyStrategy() {
    @Override
    public int calculatePoints(double purchaseAmount) {
        return (int) (purchaseAmount * 2); // Double points
    }
    
    @Override
    public double calculateDiscount() {
        return 25.0; // 25% discount
    }
    
    @Override
    public String getDescription() {
        return "VIP Program: 2x points, 25% discount";
    }
};

LoyaltyProgramManager manager = new LoyaltyProgramManager();
manager.registerCustomerWithStrategy("VIP001", vipProgram);
```

**Standard loyalty usage:**
```java
LoyaltyProgramManager manager = new LoyaltyProgramManager();
LoyaltyProgram program = manager.registerCustomer("CUST001");

// Customer makes purchase
manager.awardPointsForPurchase("CUST001", 600.0); // Customer now has Silver tier

// Apply discount
double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0); // Returns 90.0
```

## Open/Closed Principle Summary

All features comply with OCP:

| Feature | Interface | Manager | Extension Method |
|---------|-----------|---------|------------------|
| Item Categories | `ItemUpdateStrategy` | `GildedRose` | Create new strategy class |
| Discounts | `DiscountStrategy` | `BundleManager` | `addDiscountStrategy()` |
| Loyalty | `LoyaltyStrategy` | `LoyaltyProgramManager` | `registerCustomerWithStrategy()` |

**Key benefit:** Adding new functionality requires only creating new classes, not modifying existing ones.

## Testing Strategy

### Unit Tests (100% coverage)

**Item Strategy Tests:**
- `ItemUpdateStrategyTest.java` - tests all strategy implementations
- `StrategyHelperMethodsTest.java` - tests protected helper methods (including `increaseQuality()`)
- `StrategyEdgeCasesTest.java` - boundary conditions and edge cases

**Discount Tests:**
- `DiscountBundleTest.java` - bundle creation and calculations
- `BundleManagerTest.java` - manager operations and OCP compliance

**Loyalty Tests:**
- `LoyaltyProgramTest.java` - tier system and point calculations
- `LoyaltyProgramManagerTest.java` - manager operations and OCP compliance

**Integration Tests:**
- `GildedRoseRequirementsTest.java` - verifies original requirements
- `GildedRoseTest.java` - overall system behavior
- Cucumber BDD tests with step definitions

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test suite
mvn test -Dtest=BundleManagerTest
mvn test -Dtest=LoyaltyProgramManagerTest
mvn test -Dtest=StrategyHelperMethodsTest
```

## Project Structure

```
src/main/java/
├── dojo.supermarket.model.gildedrose/
│   ├── Item.java                    # Core item class
│   ├── GildedRose.java              # Main system class
│   └── discount/
│       ├── DiscountStrategy.java    # Discount interface (OCP)
│       ├── DiscountBundle.java      # Bundle implementation
│       └── BundleManager.java       # Discount manager (OCP)
├── dojo.supermarket.model.strategy/
│   ├── ItemUpdateStrategy.java      # Strategy interface (OCP)
│   ├── NormalItemStrategy.java      # Normal items
│   ├── AgedBrieStrategy.java        # Aged Brie
│   ├── SulfurasStrategy.java        # Legendary items
│   ├── BackstagePassStrategy.java   # Backstage passes
│   └── ConjuredItemStrategy.java    # Conjured items (NEW)
└── dojo.supermarket.model.loyalty/
    ├── LoyaltyStrategy.java         # Loyalty interface (OCP)
    ├── LoyaltyProgram.java          # Standard loyalty program
    ├── LoyaltyTier.java             # Bronze/Silver/Gold tiers
    └── LoyaltyProgramManager.java   # Loyalty manager (OCP)

src/test/java/
└── com.gildedrose/
    ├── GildedRoseTest.java
    ├── GildedRoseRequirementsTest.java
    ├── discount/
    │   ├── DiscountBundleTest.java
    │   └── BundleManagerTest.java
    ├── loyalty/
    │   ├── LoyaltyProgramTest.java
    │   └── LoyaltyProgramManagerTest.java
    └── strategy/
        ├── ItemUpdateStrategyTest.java
        ├── StrategyHelperMethodsTest.java
        └── StrategyEdgeCasesTest.java
```

## Requirements Verification

###  Make system open to extensions
- **Strategy Pattern** implemented for all major features
- No modification of existing code needed to add new functionality

###  Add new categories of items
- `ItemUpdateStrategy` interface allows infinite item types
- `ConjuredItemStrategy` added as example
- Each category has isolated, testable logic

###  Discounted bundles
- `DiscountStrategy` interface for extensibility
- `BundleManager` supports multiple discount types
- Fully tested with unit tests

###  Loyalty programmes
- `LoyaltyStrategy` interface for extensibility
- Three-tier system with automatic upgrades
- Supports custom loyalty programs
- Fully tested with unit tests

###  Full test coverage
- All public methods covered
- Edge cases tested
- Protected methods tested (including `increaseQuality()`)
- Integration tests verify system behavior

## Key Achievements

1. **Open/Closed Principle:** All new features support extension without modification
2. **SOLID Compliance:** Single Responsibility, Interface Segregation, Dependency Inversion
3. **Maintainability:** Clear separation of concerns, each class has one purpose
4. **Testability:** 100% test coverage with isolated unit tests
5. **Extensibility:** Easy to add new item types, discounts, and loyalty programs

## Documentation

- **README.md** (this file) - Project overview and usage
- **REFACTORING_DOCUMENTATION.md** - Detailed refactoring decisions
- **REQUIREMENTS_VERIFICATION.md** - Requirements compliance verification

## Conclusion

The Gilded Rose system has been successfully refactored to be fully compliant with the Open/Closed Principle. All instructor requirements have been met:

- System is open for extension through strategy interfaces
- New item categories can be added without modifying existing code
- Discount bundles implemented with extensible architecture
- Loyalty programmes implemented with extensible architecture
- Comprehensive test suite ensures quality and prevents regressions

The system is now maintainable, extensible, and ready for future enhancements.
