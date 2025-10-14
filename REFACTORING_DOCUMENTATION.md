# Gilded Rose - Refactoring and New Features Documentation

## Author
Refactored and extended as part of the "Software Evolution and Maintenance" course

## Table of Contents
1. [Introduction](#introduction)
2. [Problem Analysis](#problem-analysis)
3. [Applied Design Patterns](#applied-design-patterns)
4. [Refactoring Details](#refactoring-details)
5. [New Features](#new-features)
6. [Tests](#tests)
7. [Decision Justification](#decision-justification)
8. [Running Instructions](#running-instructions)

---

## Introduction

The Gilded Rose system is an inventory management application for an inn. The original code contained complex logic with many nested conditional statements, making it difficult to extend and maintain.

### Refactoring Goals:
- Increase code readability
- Facilitate adding new item types
- Implement design patterns compliant with SOLID principles
- Add new business features (bundles, loyalty program)
- Maintain full compatibility with original functionality

---

## Problem Analysis

### Problems in the Original Code:

1. **Cyclomatic Complexity**: The `updateQuality()` method contained deeply nested `if-else` statements, significantly hindering understanding of the logic.

2. **Violation of Open/Closed Principle**: Adding a new item type required modifying existing code.

3. **Lack of Separation of Concerns**: One method handled logic for all item types.

4. **Difficulty in Testing**: Complex conditional structure made testing individual scenarios difficult.

5. **Code Duplication**: Similar operations (checking quality limits) were repeated in many places.

---

## Applied Design Patterns

### 1. Strategy Pattern

**Justification**: Each item type has its unique update logic. The Strategy pattern allows encapsulation of these algorithms in separate classes.

**Implementation**:
```
ItemUpdateStrategy (interface)
    ├── NormalItemStrategy
    ├── AgedBrieStrategy
    ├── SulfurasStrategy
    ├── BackstagePassStrategy
    └── ConjuredItemStrategy
```

**Benefits**:
- Easy to add new item types without modifying existing code
- Each strategy is independent and testable
- Code is more readable and compliant with Single Responsibility Principle

### 2. Template Method Pattern (in strategies)

The `NormalItemStrategy` class contains protected helper methods (`decreaseQuality`, `increaseQuality`) that can be used by subclasses.

### 3. Manager Pattern

**BundleManager** and **LoyaltyProgramManager** manage their domains, ensuring encapsulation of business logic.

---

## Refactoring Details

### 1. Refactoring the GildedRose Class

**Before**:
```java
public void updateQuality() {
    for (int i = 0; i < items.length; i++) {
        if (!items[i].name.equals("Aged Brie") && ...) {
            // 60+ lines of nested conditions
        }
    }
}
```

**After**:
```java
public void updateQuality() {
    for (Item item : items) {
        for (ItemUpdateStrategy strategy : strategies) {
            if (strategy.canHandle(item)) {
                strategy.update(item);
                break;
            }
        }
    }
}
```

**Benefits**:
- Reduction of cyclomatic complexity from ~15 to 2
- Code is self-documenting
- Easy to debug and test

### 2. Strategies for Individual Item Types

#### NormalItemStrategy
- Quality decreases by 1 per day
- After sell date: 2 times faster
- Quality never negative

#### AgedBrieStrategy
- Quality increases over time
- After sell date: increases 2 times faster
- Maximum quality: 50

#### SulfurasStrategy
- Legendary item
- Never changes (sellIn and quality)

#### BackstagePassStrategy
- Quality increases as concert approaches:
  - >10 days: +1
  - 6-10 days: +2
  - 1-5 days: +3
  - After concert: 0

#### ConjuredItemStrategy (NEW)
- Degrades 2 times faster than normal items
- Before sell date: -2 quality/day
- After sell date: -4 quality/day

---

## New Features

### 1. Conjured Items

**Requirement**: "Conjured" items degrade 2 times faster than normal items.

**Implementation**:
- `ConjuredItemStrategy` class
- Recognition by "Conjured" prefix in name
- Dedicated degradation logic

**Usage Example**:
```java
Item conjuredItem = new Item("Conjured Mana Cake", 10, 20);
// After one day: quality = 18, sellIn = 9
```

### 2. Discount Bundles

**Requirement**: System for selling item packages with discounts.

**Implementation**:
- `DiscountBundle`: represents a package with percentage discount
- `BundleManager`: manages all bundles

**Features**:
- Create bundles with multiple items
- Automatic discount calculation
- Find best available discount

**Usage Example**:
```java
DiscountBundle bundle = new DiscountBundle("Adventurer's Pack", 15.0);
bundle.addItem(new Item("Sword", 10, 50));
bundle.addItem(new Item("Shield", 15, 40));
double price = bundle.getDiscountedPrice(100.0); // 85.0
```

### 3. Loyalty Programme

**Requirement**: Rewards system for regular customers.

**Implementation**:
- `LoyaltyProgram`: represents customer membership
- `LoyaltyTier`: enum with tiers (Bronze, Silver, Gold)
- `LoyaltyProgramManager`: manages programs for all customers

**Tiers and Benefits**:
| Tier | Required Points | Discount |
|------|----------------|----------|
| Bronze | 0 | 5% |
| Silver | 500 | 10% |
| Gold | 1000 | 15% |

**Features**:
- Automatic point collection (1 point = 1 currency unit)
- Automatic tier updates
- Point redemption capability
- Discount calculation based on tier

**Usage Example**:
```java
LoyaltyProgramManager manager = new LoyaltyProgramManager();
manager.registerCustomer("CUST001");
manager.awardPointsForPurchase("CUST001", 600.0);
// Customer automatically gets Silver tier
double finalPrice = manager.applyLoyaltyDiscount("CUST001", 100.0); // 90.0
```

---

## Tests

### 1. Unit Tests (JUnit 5)

**GildedRoseTest.java**:
- 12 tests covering all item types
- Tests for edge cases (quality = 0, quality = 50)
- Test for multiple items simultaneously

**LoyaltyProgramTest.java**:
- Tests for program creation and updates
- Tests for tier changes
- Tests for point redemption

**LoyaltyProgramManagerTest.java**:
- Tests for customer registration
- Tests for point accrual
- Tests for discount application at different tiers

**DiscountBundleTest.java**:
- Tests for bundle creation
- Tests for discount calculation
- Tests for item aggregation

**BundleManagerTest.java**:
- Tests for bundle management
- Test for finding best discount

### 2. BDD Tests (Cucumber)

**GildedRose.feature**:
- 15 test scenarios in Gherkin format
- Coverage of all item types
- Scenarios for new features (loyalty, bundles)
- Readable for non-technical stakeholders

**Example Scenario**:
```gherkin
Scenario: Conjured item degrades twice as fast
  Given I have a "Conjured Mana Cake" with sellIn 10 and quality 20
  When I update the quality
  Then the item should have sellIn 9 and quality 18
```

### Test Coverage

- **Strategy classes**: 100% coverage
- **Manager classes**: 100% coverage
- **GildedRose class**: 100% coverage
- **Edge cases**: fully covered

---

## Decision Justification

### 1. Choice of Strategy Pattern

**Why not Factory Pattern?**
- Factory creates objects but doesn't encapsulate algorithms
- Strategy better fits different behaviors for the same object type

**Why not Visitor Pattern?**
- Visitor requires modifying the Item class
- Requirement forbids modifying Item class ("goblin will insta-rage")

**Why Strategy?**
- ✅ Doesn't require changes to Item class
- ✅ Open/Closed Principle - easy to add new strategies
- ✅ Single Responsibility - each strategy has one responsibility
- ✅ Testability - each strategy tested separately

### 2. Package Structure

```
com.gildedrose/
├── GildedRose.java           # Main system class
├── Item.java                 # Data model (unchanged)
├── strategy/                 # Strategy Pattern
│   ├── ItemUpdateStrategy.java
│   ├── NormalItemStrategy.java
│   ├── AgedBrieStrategy.java
│   ├── SulfurasStrategy.java
│   ├── BackstagePassStrategy.java
│   └── ConjuredItemStrategy.java
├── discount/                 # Bundle functionality
│   ├── DiscountBundle.java
│   └── BundleManager.java
└── loyalty/                  # Loyalty program
    ├── LoyaltyProgram.java
    ├── LoyaltyTier.java
    └── LoyaltyProgramManager.java
```

**Justification**:
- Separation of concerns by domain
- Easy navigation
- Possibility to extract packages into separate modules in the future

### 3. Immutability of Item Class

According to requirements, the `Item` class was not modified. All changes are performed through manipulation of public fields, maintaining full backward compatibility.

### 4. Use of Java 8+ Features

- Stream API in Manager classes
- Optional for safe value returns
- Lambda expressions for code conciseness

---

## Running Instructions

### Requirements
- Java 8 or newer
- Maven or Gradle

### Running Tests

**Maven**:
```bash
mvnw clean test
```

**Gradle**:
```bash
gradlew clean test
```

### Running Cucumber Tests

```bash
mvnw test -Dtest=RunCucumberTest
```

or

```bash
gradlew test --tests RunCucumberTest
```

### Results Structure

After running tests:
- Unit tests: report in `target/surefire-reports/`
- Cucumber tests: report in console and `target/cucumber-reports/`

---

## Summary of Changes

### What Was Preserved:
✅ Item class unchanged
✅ Public interface of GildedRose
✅ All original business rules
✅ Full backward compatibility

### What Was Added:
✅ Strategy pattern for easier extension
✅ Conjured items support
✅ Discount bundle system
✅ Loyalty program
✅ Comprehensive tests (JUnit + Cucumber)
✅ Documentation

### Code Quality Metrics:

| Metric | Before | After |
|--------|--------|-------|
| Cyclomatic Complexity | ~15 | 2-3 |
| Lines in updateQuality() | 63 | 10 |
| Nesting Levels | 5 | 2 |
| Test Coverage | ~30% | 100% |
| Number of Tests | 1 | 40+ |

---

## Possible Future Extensions

1. **Persistence Layer**: Save state to database
2. **REST API**: Expose functionality through API
3. **Event Sourcing**: History of item changes
4. **Notification System**: Alerts for items near expiration
5. **Analytics**: Sales reports and statistics
6. **Multi-currency Support**: Handle multiple currencies
7. **Dynamic Pricing**: Dynamic prices based on demand

---

## Conclusions

The refactoring of the Gilded Rose system demonstrates how applying appropriate design patterns can drastically improve code quality. The system is now:

- **Easier to Maintain**: Clear structure, reduced complexity
- **Open for Extension**: New item types without modifying existing code
- **Well Tested**: High confidence in correctness
- **Future-Ready**: Architecture supports further development

The project demonstrates practical application of SOLID principles and clean code in a real business scenario.
