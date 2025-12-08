package dojo.supermarket.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        cart.addItemQuantity(apple, 2.5);

        assertEquals(1, cart.getItems().size());
        assertEquals(2.5, cart.productQuantities().get(apple), 0.01);
    }

    @Test
    @DisplayName("Should add multiple different items")
    void shouldAddMultipleDifferentItems() {
        cart.addItemQuantity(apple, 2.5);
        cart.addItemQuantity(banana, 1.5);

        assertEquals(2, cart.getItems().size());
        assertEquals(2, cart.productQuantities().size());
    }

    @Test
    @DisplayName("Should accumulate quantities for same product")
    void shouldAccumulateQuantitiesForSameProduct() {
        cart.addItemQuantity(apple, 2.5);
        cart.addItemQuantity(apple, 1.5);

        assertEquals(2, cart.getItems().size());
        assertEquals(4.0, cart.productQuantities().get(apple), 0.01);
    }

    @Test
    @DisplayName("Should return unmodifiable items list")
    void shouldReturnUnmodifiableItemsList() {
        cart.addItemQuantity(apple, 2.5);

        assertThrows(UnsupportedOperationException.class, () -> {
            cart.getItems().clear();
        });
    }

    @Test
    @DisplayName("Should return unmodifiable product quantities map")
    void shouldReturnUnmodifiableProductQuantitiesMap() {
        cart.addItemQuantity(apple, 2.5);

        assertThrows(UnsupportedOperationException.class, () -> {
            cart.productQuantities().clear();
        });
    }
}

