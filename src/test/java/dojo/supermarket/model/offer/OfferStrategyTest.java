package dojo.supermarket.model.offer;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.Product;
import dojo.supermarket.model.ProductUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferStrategyTest {

	@Test
	@DisplayName("ThreeForTwoStrategy should return null for quantities less than 3")
	void testThreeForTwoWithInsufficientQuantity() {
		ThreeForTwoStrategy strategy = new ThreeForTwoStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 2, 1.00, 0);

		assertNull(discount);
		assertEquals("3 for 2", strategy.getDescription());
	}

	@Test
	@DisplayName("ThreeForTwoStrategy should calculate correct discount for 6 items")
	void testThreeForTwoWithSixItems() {
		ThreeForTwoStrategy strategy = new ThreeForTwoStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 6, 1.00, 0);

		assertNotNull(discount);
		assertEquals(-2.00, discount.getDiscountAmount(), 0.01);
	}

	@Test
	@DisplayName("TwoForAmountStrategy should return null for single item")
	void testTwoForAmountWithSingleItem() {
		TwoForAmountStrategy strategy = new TwoForAmountStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 1, 2.00, 3.00);

		assertNull(discount);
		assertEquals("2 for amount", strategy.getDescription());
	}

	@Test
	@DisplayName("TwoForAmountStrategy should handle odd quantities")
	void testTwoForAmountWithOddQuantity() {
		TwoForAmountStrategy strategy = new TwoForAmountStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 3, 2.00, 3.00);

		assertNotNull(discount);
		// 3 items: 2 for 3.00 + 1 for 2.00 = 5.00, original 6.00, discount 1.00
		assertEquals(-1.00, discount.getDiscountAmount(), 0.01);
	}

	@Test
	@DisplayName("FiveForAmountStrategy should return null for less than 5 items")
	void testFiveForAmountWithInsufficientQuantity() {
		FiveForAmountStrategy strategy = new FiveForAmountStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 4, 1.00, 4.00);

		assertNull(discount);
		assertEquals("5 for amount", strategy.getDescription());
	}

	@Test
	@DisplayName("FiveForAmountStrategy should handle 7 items correctly")
	void testFiveForAmountWithSevenItems() {
		FiveForAmountStrategy strategy = new FiveForAmountStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 7, 1.00, 4.00);

		assertNotNull(discount);
		// 7 items: 5 for 4.00 + 2 for 2.00 = 6.00, original 7.00, discount 1.00
		assertEquals(-1.00, discount.getDiscountAmount(), 0.01);
	}

	@Test
	@DisplayName("PercentageDiscountStrategy should always apply discount")
	void testPercentageDiscountAlwaysApplies() {
		PercentageDiscountStrategy strategy = new PercentageDiscountStrategy();
		Product product = new Product("test", ProductUnit.EACH);

		Discount discount = strategy.calculateDiscount(product, 1, 10.00, 25.0);

		assertNotNull(discount);
		assertEquals(-2.50, discount.getDiscountAmount(), 0.01);
		assertTrue(discount.getDescription().contains("25.0% off"));
		assertEquals("Percentage discount", strategy.getDescription());
	}

	@Test
	@DisplayName("PercentageDiscountStrategy should work with fractional quantities")
	void testPercentageDiscountWithFractionalQuantity() {
		PercentageDiscountStrategy strategy = new PercentageDiscountStrategy();
		Product product = new Product("test", ProductUnit.KILO);

		Discount discount = strategy.calculateDiscount(product, 2.5, 4.00, 10.0);

		assertNotNull(discount);
		assertEquals(-1.00, discount.getDiscountAmount(), 0.01);
	}
}
