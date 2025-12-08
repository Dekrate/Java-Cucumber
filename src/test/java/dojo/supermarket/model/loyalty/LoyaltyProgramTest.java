package dojo.supermarket.model.loyalty;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Loyalty Program Tests")
class LoyaltyProgramTest {

	private Teller teller;

	private Product bread;
	private Product milk;
	private Product apples;

	@BeforeEach
	void setUp() {
		SupermarketCatalog catalog = new FakeCatalog();
		teller = new Teller(catalog);

		bread = new Product("bread", ProductUnit.EACH);
		milk = new Product("milk", ProductUnit.EACH);
		apples = new Product("apples", ProductUnit.KILO);

		catalog.addProduct(bread, bd(2.00));
		catalog.addProduct(milk, bd(1.50));
		catalog.addProduct(apples, bd(3.00));
	}

	@Test
	@DisplayName("Should earn loyalty points from purchase")
	void shouldEarnLoyaltyPointsFromPurchase() {
		LoyaltyCard card = new LoyaltyCard("LC123456");

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(2));
		cart.addItemQuantity(milk, bd(1));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

		BigDecimal expectedTotal = bd(2).multiply(bd(2.00))
				.add(bd(1).multiply(bd(1.50)));
		assertBigDecimalEquals(expectedTotal, card.getPoints());
		assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());
	}

	@Test
	@DisplayName("Should use loyalty points as payment")
	void shouldUseLoyaltyPointsAsPayment() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(100.0));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(2));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

		BigDecimal expectedTotal = bd(4.00).subtract(bd(0.50));
		assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());

		BigDecimal expectedPoints = bd(100).subtract(bd(50))
				.add(expectedTotal);
		assertBigDecimalEquals(expectedPoints, card.getPoints());
		assertEquals(1, receipt.getDiscounts().size());
	}

	@Test
	@DisplayName("Should not use more points than available")
	void shouldNotUseMorePointsThanAvailable() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(30.0));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(2));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

		BigDecimal expectedTotal = bd(4.00).subtract(bd(0.30));
		assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());
		assertBigDecimalEquals(expectedTotal, card.getPoints());
	}

	@Test
	@DisplayName("Should not use more points than total amount")
	void shouldNotUseMorePointsThanTotalAmount() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(500.0));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(1));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(500.0));

		assertBigDecimalEquals(BigDecimal.ZERO, receipt.getTotalPrice());
		assertBigDecimalEquals(bd(300.0), card.getPoints());
	}

	@Test
	@DisplayName("Should earn points with discounts applied")
	void shouldEarnPointsWithDiscountsApplied() {
		LoyaltyCard card = new LoyaltyCard("LC123456");
		teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
				milk, bd(10.0));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(milk, bd(4));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

		BigDecimal expected = bd(4).multiply(bd(1.50))
				.multiply(bd(0.9));
		assertBigDecimalEquals(expected, receipt.getTotalPrice());
		assertBigDecimalEquals(expected, card.getPoints());
	}

	@Test
	@DisplayName("Should combine loyalty points with other discounts")
	void shouldCombineLoyaltyPointsWithOtherDiscounts() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(100.0));
		teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
				apples, bd(20.0));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(apples, bd(2.0));
		cart.addItemQuantity(bread, bd(1));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(50.0));

		BigDecimal applesTotal = bd(2).multiply(bd(3.00))
				.multiply(bd(0.8));
		BigDecimal beforeLoyalty = applesTotal.add(bd(2.00));
		BigDecimal expectedTotal = beforeLoyalty.subtract(bd(0.50));
		assertBigDecimalEquals(expectedTotal, receipt.getTotalPrice());

		BigDecimal expectedPoints = bd(100).subtract(bd(50))
				.add(expectedTotal);
		assertBigDecimalEquals(expectedPoints, card.getPoints());
	}

	@Test
	@DisplayName("Should handle zero initial points")
	void shouldHandleZeroInitialPoints() {
		LoyaltyCard card = new LoyaltyCard("LC123456");

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(1));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

		assertBigDecimalEquals(bd(2.00), receipt.getTotalPrice());
		assertBigDecimalEquals(bd(2.00), card.getPoints());
	}

	@Test
	@DisplayName("Should throw exception when adding negative points")
	void shouldThrowExceptionWhenAddingNegativePoints() {
		final LoyaltyCard card = new LoyaltyCard("LC123456");
		final BigDecimal negativePoints = bd(-10);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> card.addPoints(negativePoints));
		assertNotNull(exception);
	}

	@Test
	@DisplayName("Should throw exception when using negative points")
	void shouldThrowExceptionWhenUsingNegativePoints() {
		final BigDecimal initialPoints = bd(100);
		final LoyaltyCard card = new LoyaltyCard("LC123456", initialPoints);
		final BigDecimal negativePoints = bd(-10);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> card.usePoints(negativePoints));
		assertNotNull(exception);
	}

	@Test
	@DisplayName("Should return false when using more points than available")
	void shouldReturnFalseWhenUsingMorePointsThanAvailable() {
		final BigDecimal initialPoints = bd(50);
		final BigDecimal pointsToUse = bd(100);
		final LoyaltyCard card = new LoyaltyCard("LC123456", initialPoints);
		assertFalse(card.usePoints(pointsToUse));
		assertBigDecimalEquals(initialPoints, card.getPoints());
	}

	@Test
	@DisplayName("Should throw exception with negative initial points")
	void shouldThrowExceptionWithNegativeInitialPoints() {
		final BigDecimal negativePoints = bd(-10);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> new LoyaltyCard("LC123456", negativePoints));
		assertNotNull(exception);
	}

	// ...existing code...

	@Test
	@DisplayName("LoyaltyProgramManager should throw exception for negative amount")
	void loyaltyProgramManagerShouldThrowExceptionForNegativeAmount() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal negativeAmount = bd(-10);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> manager.calculatePointsEarned(negativeAmount));
		assertNotNull(exception);
	}

	@Test
	@DisplayName("LoyaltyProgramManager should throw exception for negative points")
	void loyaltyProgramManagerShouldThrowExceptionForNegativePoints() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal negativePoints = bd(-10);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> manager.convertPointsToCurrency(negativePoints));
		assertNotNull(exception);
	}


	@Test
	@DisplayName("LoyaltyProgramManager should return null for zero points")
	void loyaltyProgramManagerShouldReturnNullForZeroPoints() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal initialPoints = bd(100);
		final BigDecimal amount = bd(100);
		final BigDecimal zeroPoints = bd(0);
		final LoyaltyCard card = new LoyaltyCard("LC123456", initialPoints);
		Discount discount = manager.applyLoyaltyPoints(card, amount, zeroPoints);
		assertNull(discount);
	}

	@Test
	@DisplayName("LoyaltyProgramManager should return null for negative points to use")
	void loyaltyProgramManagerShouldReturnNullForNegativePointsToUse() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal initialPoints = bd(100);
		final BigDecimal amount = bd(100);
		final BigDecimal negativePoints = bd(-10);
		final LoyaltyCard card = new LoyaltyCard("LC123456", initialPoints);
		Discount discount = manager.applyLoyaltyPoints(card, amount, negativePoints);
		assertNull(discount);
	}

	@Test
	@DisplayName("LoyaltyProgramManager should not credit points for null card")
	void loyaltyProgramManagerShouldNotCreditPointsForNullCard() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal amount = bd(100);
		assertDoesNotThrow(() -> manager.creditPointsForPurchase(null, amount));
	}

	@Test
	@DisplayName("LoyaltyProgramManager should not credit points for zero amount")
	void loyaltyProgramManagerShouldNotCreditPointsForZeroAmount() {
		final LoyaltyProgramManager manager = new LoyaltyProgramManager();
		final BigDecimal zeroAmount = bd(0);
		final LoyaltyCard card = new LoyaltyCard("LC123456");
		manager.creditPointsForPurchase(card, zeroAmount);
		assertBigDecimalEquals(BigDecimal.ZERO, card.getPoints());
	}

	@Test
	@DisplayName("LoyaltyProgramManager should have correct getters")
	void loyaltyProgramManagerShouldHaveCorrectGetters() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager(bd(2), bd(0.02));
		assertBigDecimalEquals(bd(2), manager.pointsPerCurrencyUnit());
		assertBigDecimalEquals(bd(0.02), manager.currencyPerPoint());
	}

	@Test
	@DisplayName("LoyaltyProgramManager should return null when card has zero points")
	void loyaltyProgramManagerShouldReturnNullWhenCardHasZeroPoints() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager();
		LoyaltyCard card = new LoyaltyCard("LC123456");
		Discount discount = manager.applyLoyaltyPoints(card, bd(100), bd(50));
		assertNull(discount);
		assertBigDecimalEquals(BigDecimal.ZERO, card.getPoints());
	}

	@Test
	@DisplayName("LoyaltyProgramManager should not credit points for negative amount")
	void loyaltyProgramManagerShouldNotCreditPointsForNegativeAmount() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager();
		LoyaltyCard card = new LoyaltyCard("LC123456");
		manager.creditPointsForPurchase(card, bd(-10));
		assertBigDecimalEquals(BigDecimal.ZERO, card.getPoints());
	}

	@Test
	@DisplayName("LoyaltyProgramManager calculatePointsEarned should handle zero amount")
	void loyaltyProgramManagerCalculatePointsEarnedShouldHandleZeroAmount() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager();
		BigDecimal points = manager.calculatePointsEarned(BigDecimal.ZERO);
		assertBigDecimalEquals(BigDecimal.ZERO, points);
	}

	@Test
	@DisplayName("LoyaltyProgramManager convertPointsToCurrency should handle zero points")
	void loyaltyProgramManagerConvertPointsToCurrencyShouldHandleZeroPoints() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager();
		BigDecimal currency = manager.convertPointsToCurrency(BigDecimal.ZERO);
		assertBigDecimalEquals(BigDecimal.ZERO, currency);
	}

	@Test
	@DisplayName("LoyaltyProgramManager should use custom conversion rates")
	void loyaltyProgramManagerShouldUseCustomConversionRates() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager(bd(5), bd(0.05));

		BigDecimal pointsEarned = manager.calculatePointsEarned(bd(10));
		assertBigDecimalEquals(bd(50), pointsEarned);

		BigDecimal currency = manager.convertPointsToCurrency(bd(100));
		assertBigDecimalEquals(bd(5), currency);
	}

	@Test
	@DisplayName("Should not earn points when purchase results in zero after full discount")
	void shouldNotEarnPointsWhenPurchaseResultsInZero() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(500));

		ShoppingCart cart = new ShoppingCart();
		cart.addItemQuantity(bread, bd(1));

		Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(500));

		assertBigDecimalEquals(BigDecimal.ZERO, receipt.getTotalPrice());
		assertBigDecimalEquals(bd(300), card.getPoints());
	}

	@Test
	@DisplayName("Should handle very small point amounts")
	void shouldHandleVerySmallPointAmounts() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager();
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(0.01));

		Discount discount = manager.applyLoyaltyPoints(card, bd(10), bd(0.01));

		assertNotNull(discount);
		assertBigDecimalEquals(BigDecimal.ZERO, card.getPoints());
	}

	@Test
	@DisplayName("Should handle rounding in points calculation")
	void shouldHandleRoundingInPointsCalculation() {
		LoyaltyProgramManager manager = new LoyaltyProgramManager(bd(1), bd(0.01));

		BigDecimal points = manager.calculatePointsEarned(bd(1.999));
		assertBigDecimalEquals(bd(2.00), points);
	}


	@Test
	@DisplayName("LoyaltyCard equals should return false for null")
	void loyaltyCardEqualsShouldReturnFalseForNull() {
		LoyaltyCard card = new LoyaltyCard("LC123456");
		assertNotEquals(null, card);
	}

	@Test
	@DisplayName("LoyaltyCard equals should return false for different class")
	void loyaltyCardEqualsShouldReturnFalseForDifferentClass() {
		LoyaltyCard card = new LoyaltyCard("LC123456");
		assertNotEquals("LC123456", card);
	}

	@Test
	@DisplayName("LoyaltyCard equals should return true for same card number")
	void loyaltyCardEqualsShouldReturnTrueForSameCardNumber() {
		LoyaltyCard card1 = new LoyaltyCard("LC123456", bd(100));
		LoyaltyCard card2 = new LoyaltyCard("LC123456", bd(200));
		assertEquals(card1, card2);
	}

	@Test
	@DisplayName("LoyaltyCard equals should return false for different card number")
	void loyaltyCardEqualsShouldReturnFalseForDifferentCardNumber() {
		LoyaltyCard card1 = new LoyaltyCard("LC123456");
		LoyaltyCard card2 = new LoyaltyCard("LC789012");
		assertNotEquals(card1, card2);
	}

	@Test
	@DisplayName("LoyaltyCard hashCode should be consistent")
	void loyaltyCardHashCodeShouldBeConsistent() {
		LoyaltyCard card1 = new LoyaltyCard("LC123456", bd(100));
		LoyaltyCard card2 = new LoyaltyCard("LC123456", bd(200));
		assertEquals(card1.hashCode(), card2.hashCode());
	}

	@Test
	@DisplayName("LoyaltyCard toString should contain card number and points")
	void loyaltyCardToStringShouldContainCardNumberAndPoints() {
		LoyaltyCard card = new LoyaltyCard("LC123456", bd(100));
		String str = card.toString();
		assertTrue(str.contains("LC123456"));
		assertTrue(str.contains("100"));
	}
}

