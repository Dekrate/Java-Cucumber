# Gilded Rose Refactoring Kata

## Overview

This is a refactored and extended version of the Gilded Rose kata, completed as part of the "Software Evolution and Maintenance" course. The system manages inventory for the Gilded Rose inn, with support for various item types, discount bundles, and a loyalty program.

## Project Structure

```
src/
├── main/java/com/gildedrose/
│   ├── GildedRose.java              # Main inventory system
│   ├── Item.java                    # Item model (unchanged)
│   ├── TexttestFixture.java         # Console demo
│   ├── strategy/                    # Strategy pattern for item updates
│   │   ├── ItemUpdateStrategy.java
│   │   ├── NormalItemStrategy.java
│   │   ├── AgedBrieStrategy.java
│   │   ├── SulfurasStrategy.java
│   │   ├── BackstagePassStrategy.java
│   │   └── ConjuredItemStrategy.java
│   ├── discount/                    # Discount bundle system
│   │   ├── DiscountBundle.java
│   │   └── BundleManager.java
│   └── loyalty/                     # Loyalty program system
│       ├── LoyaltyProgram.java
│       ├── LoyaltyTier.java
│       └── LoyaltyProgramManager.java
└── test/
    ├── java/com/gildedrose/         # JUnit tests
    └── resources/com/gildedrose/    # Cucumber features
```

## Features

### Original Features (Preserved)
- ✅ Normal items that degrade in quality
- ✅ Aged Brie that increases in quality over time
- ✅ Sulfuras (legendary item) that never changes
- ✅ Backstage passes with special quality rules
- ✅ Quality constraints (0-50 range, except Sulfuras)

### New Features
- ✅ **Conjured Items**: Degrade twice as fast as normal items
- ✅ **Discount Bundles**: Group items with percentage discounts
- ✅ **Loyalty Program**: Three-tier customer rewards (Bronze/Silver/Gold)

## Running the Project

### Prerequisites
- Java 8 or higher
- Maven or Gradle

### Build and Test

**Using Maven:**
```bash
mvnw clean test
```

**Using Gradle:**
```bash
gradlew clean test
```

### Run the Console Demo

**Using Maven:**
```bash
mvnw exec:java -Dexec.mainClass="com.gildedrose.TexttestFixture"
```

**Using Gradle:**
```bash
gradlew run
```

### Run Cucumber Tests

**Maven:**
```bash
mvnw test -Dtest=RunCucumberTest
```

**Gradle:**
```bash
gradlew test --tests RunCucumberTest
```

## Usage Examples

### Basic Inventory Management

```java
Item[] items = new Item[] {
    new Item("Normal Sword", 10, 20),
    new Item("Aged Brie", 5, 10),
    new Item("Sulfuras, Hand of Ragnaros", 0, 80),
    new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
    new Item("Conjured Mana Cake", 3, 6)
};

GildedRose app = new GildedRose(items);
app.updateQuality(); // Updates all items for one day
```

### Discount Bundles

```java
// Create a bundle with 15% discount
DiscountBundle bundle = new DiscountBundle("Starter Pack", 15.0);
bundle.addItem(new Item("Sword", 10, 50));
bundle.addItem(new Item("Shield", 15, 40));

// Calculate discounted price
double originalPrice = 100.0;
double discountedPrice = bundle.getDiscountedPrice(originalPrice); // 85.0

// Manage multiple bundles
BundleManager manager = new BundleManager();
manager.addBundle(bundle);
```

### Loyalty Program

```java
// Initialize loyalty program manager
LoyaltyProgramManager manager = new LoyaltyProgramManager();

// Register a customer
manager.registerCustomer("CUST001");

// Award points for purchase (1 point per currency unit)
manager.awardPointsForPurchase("CUST001", 600.0);

// Apply loyalty discount
double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0);
// Customer with 600 points has Silver tier (10% discount) = 90.0

// Check customer status
LoyaltyProgram program = manager.getCustomerProgram("CUST001").get();
System.out.println(program.getTier()); // SILVER
System.out.println(program.getPoints()); // 600
```

### Loyalty Tiers

| Tier   | Points Required | Discount |
|--------|----------------|----------|
| Bronze | 0              | 5%       |
| Silver | 500            | 10%      |
| Gold   | 1000           | 15%      |

## Item Update Rules

### Normal Items
- Quality decreases by 1 per day
- After sell date: decreases by 2 per day
- Quality never negative

### Aged Brie
- Quality increases by 1 per day
- After sell date: increases by 2 per day
- Quality never exceeds 50

### Sulfuras (Legendary)
- Never changes (quality stays at 80)
- Never needs to be sold

### Backstage Passes
- More than 10 days: quality +1
- 6-10 days: quality +2
- 1-5 days: quality +3
- After concert: quality drops to 0

### Conjured Items (NEW)
- Quality decreases by 2 per day
- After sell date: decreases by 4 per day
- Quality never negative

## Testing

### Test Coverage
- **85+ Unit Tests** (JUnit 5)
- **15 BDD Scenarios** (Cucumber)
- **~95% Code Coverage**

### Run All Tests
```bash
# Maven
mvnw clean test

# Gradle
gradlew clean test
```

### Test Categories
1. **GildedRoseTest**: Core functionality tests
2. **ItemUpdateStrategyTest**: Strategy-specific tests
3. **GildedRoseRequirementsTest**: Requirements verification
4. **LoyaltyProgramTest**: Loyalty program tests
5. **DiscountBundleTest**: Bundle system tests
6. **Cucumber Features**: BDD acceptance tests

## Design Patterns

### Strategy Pattern
Each item type has its own strategy for quality updates:
- Encapsulates update algorithms
- Easy to add new item types
- Follows Open/Closed Principle

### Manager Pattern
Centralized management for complex subsystems:
- BundleManager: Handles discount bundles
- LoyaltyProgramManager: Manages customer loyalty

## Documentation

- **REFACTORING_DOCUMENTATION.md**: Detailed refactoring explanation
- **REQUIREMENTS_VERIFICATION.md**: Requirements compliance checklist
- **README.md**: This file

## Code Quality Improvements

| Metric | Before | After |
|--------|--------|-------|
| Cyclomatic Complexity | 15 | 2-3 |
| Lines in updateQuality() | 63 | 10 |
| Nesting Levels | 5 | 2 |
| Test Coverage | ~30% | ~95% |
| Number of Tests | 1 | 85+ |

## Adding New Item Types

Thanks to the Strategy pattern, adding new item types is simple:

```java
// 1. Create a new strategy
public class MagicItemStrategy implements ItemUpdateStrategy {
    @Override
    public boolean canHandle(Item item) {
        return item.name.startsWith("Magic");
    }
    
    @Override
    public void update(Item item) {
        // Custom update logic
    }
}

// 2. Register in GildedRose.initializeStrategies()
strategies.add(new MagicItemStrategy());
```

## License

This is an educational project based on the Gilded Rose kata.

## Author

Refactored and extended for "Software Evolution and Maintenance" course.

### How to do the BDD

1. Write a test scenario in Feature file: **src/test/resources/GildedRose.feature**
2. Modify the StepDefinitions file to match the Feature description in: **src/test/java/com/gildedrose/StepDefinitions.java**
3. Run: **./gradlew cucumber** from project dir

Note: Please check https://cucumber.io for syntax references.