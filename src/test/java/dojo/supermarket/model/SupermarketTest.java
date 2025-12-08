package dojo.supermarket.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SupermarketTest {

    @Test
    void tenPercentDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, bd(0.99));
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, bd(1.99));

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT,
                toothbrush, bd(10.0));

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, bd(2.5));

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertBigDecimalEquals(bd(4.975), receipt.getTotalPrice());
        assertEquals(Collections.emptyList(), receipt.getDiscounts());
        assertEquals(1, receipt.getItems().size());
        ReceiptItem receiptItem = receipt.getItems().getFirst();
        assertEquals(apples, receiptItem.product());
        assertBigDecimalEquals(bd(1.99), receiptItem.price());
        BigDecimal expectedTotal = bd(2.5).multiply(bd(1.99));
        assertBigDecimalEquals(expectedTotal, receiptItem.totalPrice());
        assertBigDecimalEquals(bd(2.5), receiptItem.quantity());
    }
}

