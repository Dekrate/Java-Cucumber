package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertEquals(0.0, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should calculate total from items")
    void shouldCalculateTotalFromItems() {
        receipt.addProduct(apple, 2.0, 1.99, 3.98);
        receipt.addProduct(banana, 3.0, 0.50, 1.50);

        assertEquals(5.48, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should calculate total with discounts")
    void shouldCalculateTotalWithDiscounts() {
        receipt.addProduct(apple, 2.0, 1.99, 3.98);
        receipt.addDiscount(new Discount(apple, "10% off", -0.40));

        assertEquals(3.58, receipt.getTotalPrice(), 0.01);
    }

    @Test
    @DisplayName("Should store multiple discounts")
    void shouldStoreMultipleDiscounts() {
        receipt.addDiscount(new Discount(apple, "10% off", -0.40));
        receipt.addDiscount(new Discount(banana, "3 for 2", -0.50));

        assertEquals(2, receipt.getDiscounts().size());
    }

    @Test
    @DisplayName("Should return items list")
    void shouldReturnItemsList() {
        receipt.addProduct(apple, 2.0, 1.99, 3.98);

        assertEquals(1, receipt.getItems().size());
        assertEquals(apple, receipt.getItems().get(0).product());
    }

    @Test
    @DisplayName("Should return unmodifiable items list")
    void shouldReturnUnmodifiableItemsList() {
        receipt.addProduct(apple, 2.0, 1.99, 3.98);

        assertThrows(UnsupportedOperationException.class, () -> {
            receipt.getItems().clear();
        });
    }
}

