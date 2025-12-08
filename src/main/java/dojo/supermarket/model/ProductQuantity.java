package dojo.supermarket.model;

import java.math.BigDecimal;

public record ProductQuantity(Product product, BigDecimal quantity) {
}
