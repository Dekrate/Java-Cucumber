package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Receipt Tests")
class ReceiptTest {

    private Receipt receipt;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        receipt = new Receipt();
        apple = new Product("apple", ProductUnit.KILO);
        banana = new Product("banana", ProductUnit.EACH);
    }

    @Test
    @DisplayName("Should start with zero total")
    void shouldStartWithZeroTotal() {
        assertBigDecimalEquals(BigDecimal.ZERO, receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should calculate total from items")
    void shouldCalculateTotalFromItems() {
        receipt.addProduct(apple, bd(2.0), bd(1.99), bd(3.98));
        receipt.addProduct(banana, bd(3.0), bd(0.50), bd(1.50));

        assertBigDecimalEquals(bd(5.48), receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should calculate total with discounts")
    void shouldCalculateTotalWithDiscounts() {
        receipt.addProduct(apple, bd(2.0), bd(1.99), bd(3.98));
        receipt.addDiscount(new Discount(apple, "10% off", bd(-0.40)));

        assertBigDecimalEquals(bd(3.58), receipt.getTotalPrice());
    }

    @Test
    @DisplayName("Should store multiple discounts")
    void shouldStoreMultipleDiscounts() {
        receipt.addDiscount(new Discount(apple, "10% off", bd(-0.40)));
        receipt.addDiscount(new Discount(banana, "3 for 2", bd(-0.50)));

        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should return items list")
    void shouldReturnItemsList() {
        receipt.addProduct(apple, bd(2.0), bd(1.99), bd(3.98));

        assertEquals(1, receipt.getItems().size());
        assertEquals(apple, receipt.getItems().get(0).product());
    }

    @Test
    @DisplayName("Should return unmodifiable items list")
    void shouldReturnUnmodifiableItemsList() {
        receipt.addProduct(apple, bd(2.0), bd(1.99), bd(3.98));

        assertThrows(UnsupportedOperationException.class, () -> {
            receipt.getItems().clear();
        });
    }
}

