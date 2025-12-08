package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dojo.supermarket.model.TestHelper.assertBigDecimalEquals;
import static dojo.supermarket.model.TestHelper.bd;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ShoppingCart Tests")
class ShoppingCartTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        apple = new Product("apple", ProductUnit.KILO);
        banana = new Product("banana", ProductUnit.KILO);
    }

    @Test
    @DisplayName("Should start with empty cart")
    void shouldStartWithEmptyCart() {
        assertTrue(cart.getItems().isEmpty());
        assertTrue(cart.productQuantities().isEmpty());
    }

    @Test
    @DisplayName("Should add single item")
    void shouldAddSingleItem() {
        cart.addItemQuantity(apple, bd(2.5));

        assertEquals(1, cart.getItems().size());
        assertBigDecimalEquals(bd(2.5), cart.productQuantities().get(apple));
    }

    @Test
    @DisplayName("Should add multiple different items")
    void shouldAddMultipleDifferentItems() {
        cart.addItemQuantity(apple, bd(2.5));
        cart.addItemQuantity(banana, bd(1.5));

        assertEquals(2, cart.getItems().size());
        assertEquals(2, cart.productQuantities().size());
    }

    @Test
    @DisplayName("Should accumulate quantities for same product")
    void shouldAccumulateQuantitiesForSameProduct() {
        cart.addItemQuantity(apple, bd(2.5));
        cart.addItemQuantity(apple, bd(1.5));

        assertEquals(2, cart.getItems().size());
        assertBigDecimalEquals(bd(4.0), cart.productQuantities().get(apple));
    }

    @Test
    @DisplayName("Should return unmodifiable items list")
    void shouldReturnUnmodifiableItemsList() {
        final var quantity = bd(2.5);
        cart.addItemQuantity(apple, quantity);

        final var items = cart.getItems();
        assertThrows(UnsupportedOperationException.class, items::clear);
    }

    @Test
    @DisplayName("Should return unmodifiable product quantities map")
    void shouldReturnUnmodifiableProductQuantitiesMap() {
        final var quantity = bd(2.5);
        cart.addItemQuantity(apple, quantity);

        final var quantities = cart.productQuantities();
        assertThrows(UnsupportedOperationException.class, quantities::clear);
    }
}

