package dojo.supermarket.model;

import dojo.supermarket.model.category.StandardCategory;
import java.util.Objects;

public class Product {

    private final String name;
    private final ProductUnit unit;
    private ProductCategory category;

    public Product(String name, ProductUnit unit) {
        this.name = name;
        this.unit = unit;
        this.category = new StandardCategory(); // Default category
    }

    public Product(String name, ProductUnit unit, ProductCategory category) {
        this.name = name;
        this.unit = unit;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public ProductUnit getUnit() {
        return unit;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) &&
                unit == product.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unit);
    }
}
