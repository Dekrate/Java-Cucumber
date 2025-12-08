package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teller Tests")
class TellerTest {

    private SupermarketCatalog catalog;
    private Teller teller;

    private Product toothbrush;
    private Product apples;
    private Product rice;
    private Product toothpaste;
    private Product cherryTomatoes;

    @BeforeEach
    void setUp() {
        catalog = new FakeCatalog();
        teller = new Teller(catalog);

        // Setup products
        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);
        rice = new Product("rice", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        cherryTomatoes = new Product("cherry tomatoes", ProductUnit.EACH);

        // Add to catalog
        catalog.addProduct(toothbrush, 0.99);
        catalog.addProduct(apples, 1.99);
        catalog.addProduct(rice, 2.49);
        catalog.addProduct(toothpaste, 1.79);
        catalog.addProduct(cherryTomatoes, 0.69);
    }

    @Test
    @DisplayName("Should calculate total without any discounts")
    void shouldCalculateTotalWithoutDiscounts() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2.5 * 1.99 + 0.99;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply three for two discount")
    void shouldApplyThreeForTwoDiscount() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Pay for 2, get 3
        double expected = 2 * 0.99;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("3 for 2", receipt.getDiscounts().get(0).description());
    }

    @Test
    @DisplayName("Should apply three for two discount multiple times")
    void shouldApplyThreeForTwoDiscountMultipleTimes() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 7);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 7 items: 2 sets of 3-for-2 (pay for 4) + 1 regular (pay for 1) = pay for 5
        double expected = 5 * 0.99;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should apply ten percent discount")
    void shouldApplyTenPercentDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, rice, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(rice, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2 * 2.49 * 0.9; // 10% off
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should apply twenty percent discount on apples")
    void shouldApplyTwentyPercentDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2.5 * 1.99 * 0.8; // 20% off
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should apply two for amount discount")
    void shouldApplyTwoForAmountDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cherryTomatoes, 0.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should apply two for amount discount with odd quantity")
    void shouldApplyTwoForAmountDiscountOddQuantity() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cherryTomatoes, 0.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, 5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 5 items: 2 sets of 2 (2*0.99) + 1 regular (0.69)
        double expected = 2 * 0.99 + 0.69;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should apply five for amount discount")
    void shouldApplyFiveForAmountDiscount() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, toothpaste, 7.49);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(7.49, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should not apply five for amount discount when quantity is less than 5")
    void shouldNotApplyFiveForAmountDiscountWhenInsufficientQuantity() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, toothpaste, 7.49);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 4);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 4 * 1.79;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply five for amount discount multiple times")
    void shouldApplyFiveForAmountDiscountMultipleTimes() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, toothpaste, 7.49);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 12);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // 12 items: 2 sets of 5 (2*7.49) + 2 regular (2*1.79)
        double expected = 2 * 7.49 + 2 * 1.79;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should apply multiple different discounts")
    void shouldApplyMultipleDifferentDiscounts() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, rice, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3);
        cart.addItemQuantity(rice, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2 * 0.99 + 2 * 2.49 * 0.9;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }

    @ParameterizedTest
    @CsvSource({
        "1, 0.99",
        "2, 1.98",
        "3, 1.98",
        "4, 2.97",
        "5, 3.96",
        "6, 3.96"
    })
    @DisplayName("Should correctly calculate three for two with various quantities")
    void shouldCorrectlyCalculateThreeForTwo(int quantity, double expectedTotal) {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, quantity);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(expectedTotal, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should handle empty cart")
    void shouldHandleEmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.0, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getItems().isEmpty());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should calculate correct receipt items")
    void shouldCalculateCorrectReceiptItems() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.0);
        cart.addItemQuantity(toothbrush, 1.0);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(2, receipt.getItems().size());

        ReceiptItem applesItem = receipt.getItems().stream()
            .filter(item -> item.product().equals(apples))
            .findFirst()
            .orElseThrow();

        assertEquals(2.0, applesItem.quantity(), 0.01);
        assertEquals(1.99, applesItem.price(), 0.01);
        assertEquals(3.98, applesItem.totalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should not apply three for two discount for non-whole quantities")
    void shouldNotApplyThreeForTwoDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3.5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 3.5 * 0.99;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should not apply two for amount discount for non-whole quantities")
    void shouldNotApplyTwoForAmountDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cherryTomatoes, 0.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, 2.3);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2.3 * 0.69;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should not apply five for amount discount for non-whole quantities")
    void shouldNotApplyFiveForAmountDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, toothpaste, 7.49);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 5.7);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 5.7 * 1.79;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply percentage discount for non-whole quantities")
    void shouldApplyPercentageDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        double expected = 2.5 * 1.99 * 0.8;
        assertEquals(expected, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
    }
}

