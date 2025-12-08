package dojo.supermarket.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FakeCatalog implements SupermarketCatalog {
    private final Map<String, BigDecimal> prices = new HashMap<>();

    @Override
    public void addProduct(final Product product, final BigDecimal price) {
        this.prices.put(product.name(), price);
    }

    @Override
    public BigDecimal getUnitPrice(final Product p) {
        return this.prices.get(p.name());
    }
}

