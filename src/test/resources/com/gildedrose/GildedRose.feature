Feature: Gilded Rose quality and inventory management
  As the Gilded Rose inn keeper
  I want to properly manage my inventory
  So that items update correctly based on their type

  Scenario: Normal item degrades in quality
    Given I have a "Normal Item" with sellIn 10 and quality 20
    When I update the quality
    Then the item should have sellIn 9 and quality 19

  Scenario: Normal item degrades twice as fast after sell date
    Given I have a "Normal Item" with sellIn 0 and quality 20
    When I update the quality
    Then the item should have sellIn -1 and quality 18

  Scenario: Quality never goes negative
    Given I have a "Normal Item" with sellIn 5 and quality 0
    When I update the quality
    Then the item should have sellIn 4 and quality 0

  Scenario: Aged Brie increases in quality
    Given I have a "Aged Brie" with sellIn 10 and quality 20
    When I update the quality
    Then the item should have sellIn 9 and quality 21

  Scenario: Quality never exceeds 50
    Given I have a "Aged Brie" with sellIn 10 and quality 50
    When I update the quality
    Then the item should have sellIn 9 and quality 50

  Scenario: Sulfuras never changes
    Given I have a "Sulfuras, Hand of Ragnaros" with sellIn 10 and quality 80
    When I update the quality
    Then the item should have sellIn 10 and quality 80

  Scenario: Backstage pass increases by 2 when 10 days or less
    Given I have a "Backstage passes to a TAFKAL80ETC concert" with sellIn 10 and quality 20
    When I update the quality
    Then the item should have sellIn 9 and quality 22

  Scenario: Backstage pass increases by 3 when 5 days or less
    Given I have a "Backstage passes to a TAFKAL80ETC concert" with sellIn 5 and quality 20
    When I update the quality
    Then the item should have sellIn 4 and quality 23

  Scenario: Backstage pass drops to 0 after concert
    Given I have a "Backstage passes to a TAFKAL80ETC concert" with sellIn 0 and quality 20
    When I update the quality
    Then the item should have sellIn -1 and quality 0

  Scenario: Conjured item degrades twice as fast
    Given I have a "Conjured Mana Cake" with sellIn 10 and quality 20
    When I update the quality
    Then the item should have sellIn 9 and quality 18

  Scenario: Conjured item degrades four times as fast after sell date
    Given I have a "Conjured Mana Cake" with sellIn 0 and quality 20
    When I update the quality
    Then the item should have sellIn -1 and quality 16

  Scenario: Customer earns loyalty points
    Given I have a customer "CUST001" with 0 loyalty points
    When the customer makes a purchase of 150.0
    Then the customer should have 150 loyalty points

  Scenario: Customer upgrades to Silver tier
    Given I have a customer "CUST002" with 0 loyalty points
    When the customer makes a purchase of 500.0
    Then the customer should have tier "SILVER"
    And the customer discount should be 10.0 percent

  Scenario: Customer upgrades to Gold tier
    Given I have a customer "CUST003" with 0 loyalty points
    When the customer makes a purchase of 1000.0
    Then the customer should have tier "GOLD"
    And the customer discount should be 15.0 percent

  Scenario: Discount bundle applies correct discount
    Given I have a bundle "Adventurer's Pack" with 15.0 percent discount
    When I calculate the discounted price for 100.0
    Then the final price should be 85.0
