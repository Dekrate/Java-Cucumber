package com.gildedrose;

import static org.junit.Assert.*;

import com.gildedrose.discount.DiscountBundle;
import com.gildedrose.loyalty.LoyaltyProgramManager;
import com.gildedrose.loyalty.LoyaltyProgram;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefinitions {
    private Item[] items = new Item[1];
    private GildedRose app;
    private LoyaltyProgramManager loyaltyManager;
    private String currentCustomerId;
    private DiscountBundle currentBundle;
    private double calculatedPrice;

    @Given("I have a {string} with sellIn {int} and quality {int}")
    public void i_have_an_item_with_sellin_and_quality(String name, Integer sellIn, Integer quality) {
        items[0] = new Item(name, sellIn, quality);
        app = new GildedRose(items);
    }

    @When("I update the quality")
    public void i_update_the_quality() {
        app.updateQuality();
    }

    @Then("the item should have sellIn {int} and quality {int}")
    public void the_item_should_have_sellin_and_quality(Integer expectedSellIn, Integer expectedQuality) {
        assertEquals(expectedSellIn.intValue(), app.items[0].sellIn);
        assertEquals(expectedQuality.intValue(), app.items[0].quality);
    }

    @Given("I have a customer {string} with {int} loyalty points")
    public void i_have_a_customer_with_loyalty_points(String customerId, Integer points) {
        loyaltyManager = new LoyaltyProgramManager();
        currentCustomerId = customerId;
        LoyaltyProgram program = loyaltyManager.registerCustomer(customerId);
        if (points > 0) {
            program.addPoints(points);
        }
    }

    @When("the customer makes a purchase of {double}")
    public void the_customer_makes_a_purchase_of(Double amount) {
        loyaltyManager.awardPointsForPurchase(currentCustomerId, amount);
    }

    @Then("the customer should have {int} loyalty points")
    public void the_customer_should_have_loyalty_points(Integer expectedPoints) {
        LoyaltyProgram program = loyaltyManager.getCustomerProgram(currentCustomerId).get();
        assertEquals(expectedPoints.intValue(), program.getPoints());
    }

    @Then("the customer should have tier {string}")
    public void the_customer_should_have_tier(String expectedTier) {
        LoyaltyProgram program = loyaltyManager.getCustomerProgram(currentCustomerId).get();
        assertEquals(expectedTier, program.getTier().toString());
    }

    @Then("the customer discount should be {double} percent")
    public void the_customer_discount_should_be_percent(Double expectedDiscount) {
        LoyaltyProgram program = loyaltyManager.getCustomerProgram(currentCustomerId).get();
        assertEquals(expectedDiscount, program.getTierDiscount(), 0.01);
    }

    @Given("I have a bundle {string} with {double} percent discount")
    public void i_have_a_bundle_with_percent_discount(String bundleName, Double discount) {
        currentBundle = new DiscountBundle(bundleName, discount);
    }

    @When("I calculate the discounted price for {double}")
    public void i_calculate_the_discounted_price_for(Double basePrice) {
        calculatedPrice = currentBundle.getDiscountedPrice(basePrice);
    }

    @Then("the final price should be {double}")
    public void the_final_price_should_be(Double expectedPrice) {
        assertEquals(expectedPrice, calculatedPrice, 0.01);
    }
}
