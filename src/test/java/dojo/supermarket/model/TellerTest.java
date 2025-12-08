package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teller Tests")
class TellerTest {

	private Teller teller;

    private Product toothbrush;
    private Product apples;
    private Product rice;
    private Product toothpaste;
    private Product cherryTomatoes;
    private Product bread;

    @BeforeEach
    void setUp() {
	    SupermarketCatalog catalog = new FakeCatalog();
        teller = new Teller(catalog);

        toothbrush = new Product("toothbrush", ProductUnit.EACH);
        apples = new Product("apples", ProductUnit.KILO);
        rice = new Product("rice", ProductUnit.EACH);
        toothpaste = new Product("toothpaste", ProductUnit.EACH);
        cherryTomatoes = new Product("cherry tomatoes", ProductUnit.EACH);
        bread = new Product("bread", ProductUnit.EACH);

        catalog.addProduct(toothbrush, bd(0.99));
        catalog.addProduct(apples, bd(1.99));
        catalog.addProduct(rice, bd(2.49));
        catalog.addProduct(toothpaste, bd(1.79));
        catalog.addProduct(cherryTomatoes, bd(0.69));
        catalog.addProduct(bread, bd(2.00));
    }

    @Test
    @DisplayName("Should calculate total without any discounts")
    void shouldCalculateTotalWithoutDiscounts() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.5));
        cart.addItemQuantity(toothbrush, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2.5).multiply(bd(1.99)).add(bd(0.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply three for two discount")
    void shouldApplyThreeForTwoDiscount() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(3));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(0.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("3 for 2", receipt.getDiscounts().getFirst().description());
    }

    @Test
    @DisplayName("Should apply three for two discount multiple times")
    void shouldApplyThreeForTwoDiscountMultipleTimes() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(7));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(5).multiply(bd(0.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply ten percent discount")
    void shouldApplyTenPercentDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                rice, bd(10.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(rice, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(2.49))
                .multiply(bd(0.9));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should apply twenty percent discount on apples")
    void shouldApplyTwentyPercentDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                apples, bd(20.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2.5).multiply(bd(1.99))
                .multiply(bd(0.8));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply two for amount discount")
    void shouldApplyTwoForAmountDiscount() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT,
                cherryTomatoes, bd(0.99));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(0.99), receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should apply two for amount discount with odd quantity")
    void shouldApplyTwoForAmountDiscountOddQuantity() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT,
                cherryTomatoes, bd(0.99));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, bd(5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(0.99)).add(bd(0.69));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply five for amount discount")
    void shouldApplyFiveForAmountDiscount() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT,
                toothpaste, bd(7.49));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, bd(5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(7.49), receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should not apply five for amount discount when quantity is less than 5")
    void shouldNotApplyFiveForAmountDiscountWhenInsufficientQuantity() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT,
                toothpaste, bd(7.49));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, bd(4));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(4).multiply(bd(1.79));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply five for amount discount multiple times")
    void shouldApplyFiveForAmountDiscountMultipleTimes() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT,
                toothpaste, bd(7.49));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, bd(12));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(7.49))
                .add(bd(2).multiply(bd(1.79)));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply multiple different discounts")
    void shouldApplyMultipleDifferentDiscounts() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                rice, bd(10.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(3));
        cart.addItemQuantity(rice, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2).multiply(bd(0.99))
                .add(bd(2).multiply(bd(2.49)).multiply(bd(0.9)));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
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
    void shouldCorrectlyCalculateThreeForTwo(final int quantity,
                                             final double expectedTotal) {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(quantity));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(expectedTotal), receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle empty cart")
    void shouldHandleEmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(BigDecimal.ZERO, receipt.getTotalPrice());
        assertTrue(receipt.getItems().isEmpty());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should calculate correct receipt items")
    void shouldCalculateCorrectReceiptItems() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.0));
        cart.addItemQuantity(toothbrush, bd(1.0));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(2, receipt.getItems().size());

        ReceiptItem applesItem = receipt.getItems().stream()
            .filter(item -> item.product().equals(apples))
            .findFirst()
            .orElseThrow();

        assertBigDecimalEquals(bd(2.0), applesItem.quantity());
        assertBigDecimalEquals(bd(1.99), applesItem.price());
        assertBigDecimalEquals(bd(3.98), applesItem.totalPrice());
    }

    @Test
    @DisplayName("Should not apply three for two discount for non-whole quantities")
    void shouldNotApplyThreeForTwoDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO,
                toothbrush, bd(0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(3.5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(3.5).multiply(bd(0.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should not apply two for amount discount for non-whole quantities")
    void shouldNotApplyTwoForAmountDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT,
                cherryTomatoes, bd(0.99));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, bd(2.3));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2.3).multiply(bd(0.69));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should not apply five for amount discount for non-whole quantities")
    void shouldNotApplyFiveForAmountDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT,
                toothpaste, bd(7.49));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, bd(5.7));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(5.7).multiply(bd(1.79));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should apply percentage discount for non-whole quantities")
    void shouldApplyPercentageDiscountForNonWholeQuantities() {
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                apples, bd(20.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal expected = bd(2.5).multiply(bd(1.99))
                .multiply(bd(0.8));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(1, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should not apply two for amount discount when quantity is less than 2")
    void shouldNotApplyTwoForAmountDiscountWhenQuantityLessThan2() {
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT,
                cherryTomatoes, bd(0.99));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(0.69), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should get loyalty manager")
    void shouldGetLoyaltyManager() {
        assertNotNull(teller.getLoyaltyManager());
    }

    @Test
    @DisplayName("Should handle checkout without loyalty card")
    void shouldHandleCheckoutWithoutLoyaltyCard() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(bread, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, null, bd(0));

        assertBigDecimalEquals(bd(2.00), receipt.getTotalPrice());
        assertTrue(receipt.getDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Should set and use purchase date for coupons")
    void shouldSetAndUsePurchaseDateForCoupons() {
        teller.setPurchaseDate(java.time.LocalDate.of(2025, 11, 14));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(1.99), receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle checkout with loyalty card but zero points")
    void shouldHandleCheckoutWithLoyaltyCardButZeroPoints() {
        dojo.supermarket.model.loyalty.LoyaltyCard card =
                new dojo.supermarket.model.loyalty.LoyaltyCard("LC123456");

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(0));

        BigDecimal expected = bd(2).multiply(bd(1.99));
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertBigDecimalEquals(expected, card.getPoints());
    }

    @Test
    @DisplayName("Should handle checkout with loyalty card and negative points to use")
    void shouldHandleCheckoutWithLoyaltyCardAndNegativePointsToUse() {
        dojo.supermarket.model.loyalty.LoyaltyCard card =
                new dojo.supermarket.model.loyalty.LoyaltyCard("LC123456", bd(100));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(-10));

        BigDecimal expected = bd(1.99);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        BigDecimal expectedPoints = bd(100).add(expected);
        assertBigDecimalEquals(expectedPoints, card.getPoints());
    }

    @Test
    @DisplayName("Should handle checkout without loyalty card and zero points")
    void shouldHandleCheckoutWithoutLoyaltyCardAndZeroPoints() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(rice, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart, null, BigDecimal.ZERO);

        assertBigDecimalEquals(bd(2.49), receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should apply bundle and then regular offer")
    void shouldApplyBundleAndThenRegularOffer() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        dojo.supermarket.model.bundle.ProductBundle bundle =
                dojo.supermarket.model.bundle.ProductBundle.withDefaultDiscount(
                        "Dental Bundle", bundleProducts);

        teller.addProductBundle(bundle);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, rice, bd(10.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));
        cart.addItemQuantity(rice, bd(2));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal bundlePrice = bd(0.99).add(bd(1.79)).multiply(bd(0.9));
        BigDecimal ricePrice = bd(2).multiply(bd(2.49)).multiply(bd(0.9));
        BigDecimal expected = bundlePrice.add(ricePrice);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should apply coupon and then regular offer")
    void shouldApplyCouponAndThenRegularOffer() {
        dojo.supermarket.model.coupon.Coupon coupon =
                new dojo.supermarket.model.coupon.Coupon(
                        "COUP123",
                        apples,
                        2,
                        2,
                        bd(50),
                        java.time.LocalDate.of(2025, 12, 1),
                        java.time.LocalDate.of(2025, 12, 31)
                );

        teller.addCoupon(coupon);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, bd(0));
        teller.setPurchaseDate(java.time.LocalDate.of(2025, 12, 8));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(4));
        cart.addItemQuantity(toothbrush, bd(3));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal applesTotal = bd(4).multiply(bd(1.99));
        BigDecimal couponDiscount = bd(2).multiply(bd(1.99)).multiply(bd(0.5));
        BigDecimal toothbrushTotal = bd(2).multiply(bd(0.99));
        BigDecimal expected = applesTotal.add(toothbrushTotal).subtract(couponDiscount);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle multiple bundles")
    void shouldHandleMultipleBundles() {
        Map<Product, Integer> bundle1Products = new HashMap<>();
        bundle1Products.put(toothbrush, 1);
        bundle1Products.put(toothpaste, 1);

        Map<Product, Integer> bundle2Products = new HashMap<>();
        bundle2Products.put(apples, 1);
        bundle2Products.put(rice, 1);

        dojo.supermarket.model.bundle.ProductBundle bundle1 =
                dojo.supermarket.model.bundle.ProductBundle.withDefaultDiscount(
                        "Dental Bundle", bundle1Products);
        dojo.supermarket.model.bundle.ProductBundle bundle2 =
                dojo.supermarket.model.bundle.ProductBundle.withDefaultDiscount(
                        "Food Bundle", bundle2Products);

        teller.addProductBundle(bundle1);
        teller.addProductBundle(bundle2);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));
        cart.addItemQuantity(apples, bd(1));
        cart.addItemQuantity(rice, bd(1));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        BigDecimal dentalBundle = bd(0.99).add(bd(1.79)).multiply(bd(0.9));
        BigDecimal foodBundle = bd(1.99).add(bd(2.49)).multiply(bd(0.9));
        BigDecimal expected = dentalBundle.add(foodBundle);
        assertBigDecimalEquals(expected, receipt.getTotalPrice());
        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should handle complex scenario with all discount types")
    void shouldHandleComplexScenarioWithAllDiscountTypes() {
        Map<Product, Integer> bundleProducts = new HashMap<>();
        bundleProducts.put(toothbrush, 1);
        bundleProducts.put(toothpaste, 1);
        dojo.supermarket.model.bundle.ProductBundle bundle =
                dojo.supermarket.model.bundle.ProductBundle.withDefaultDiscount(
                        "Dental Bundle", bundleProducts);

        dojo.supermarket.model.coupon.Coupon coupon =
                new dojo.supermarket.model.coupon.Coupon(
                        "COUP456",
                        apples,
                        2,
                        2,
                        bd(25),
                        java.time.LocalDate.of(2025, 12, 1),
                        java.time.LocalDate.of(2025, 12, 31)
                );

        dojo.supermarket.model.loyalty.LoyaltyCard card =
                new dojo.supermarket.model.loyalty.LoyaltyCard("LC999", bd(50));

        teller.addProductBundle(bundle);
        teller.addCoupon(coupon);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, rice, bd(0));
        teller.setPurchaseDate(java.time.LocalDate.of(2025, 12, 8));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, bd(1));
        cart.addItemQuantity(toothpaste, bd(1));
        cart.addItemQuantity(apples, bd(4));
        cart.addItemQuantity(rice, bd(3));

        Receipt receipt = teller.checksOutArticlesFrom(cart, card, bd(25));

        assertNotNull(receipt);
        assertTrue(receipt.getDiscounts().size() >= 3);
    }
}

