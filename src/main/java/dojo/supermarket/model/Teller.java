package dojo.supermarket.model;

import dojo.supermarket.model.bundle.BundleManager;
import dojo.supermarket.model.loyalty.LoyaltyProgramManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teller {

    private final SupermarketCatalog catalog;
    private final Map<Product, Offer> offers = new HashMap<>();
    private final BundleManager bundleManager = new BundleManager();
    private final LoyaltyProgramManager loyaltyManager = new LoyaltyProgramManager();
    private boolean loyaltyProgramEnabled = false;

    public Teller(SupermarketCatalog catalog) {
        this.catalog = catalog;
    }

    public void addSpecialOffer(SpecialOfferType offerType, Product product, double argument) {
        offers.put(product, new Offer(offerType, product, argument));
    }

    public BundleManager getBundleManager() {
        return bundleManager;
    }

    public LoyaltyProgramManager getLoyaltyManager() {
        return loyaltyManager;
    }

    public void enableLoyaltyProgram() {
        this.loyaltyProgramEnabled = true;
    }

    public void disableLoyaltyProgram() {
        this.loyaltyProgramEnabled = false;
    }

    public Receipt checksOutArticlesFrom(ShoppingCart theCart) {
        Receipt receipt = new Receipt();
        List<ProductQuantity> productQuantities = theCart.getItems();
        for (ProductQuantity pq: productQuantities) {
            Product p = pq.getProduct();
            double quantity = pq.getQuantity();
            double unitPrice = catalog.getUnitPrice(p);
            double price = quantity * unitPrice;
            receipt.addProduct(p, quantity, unitPrice, price);
        }

        // Apply special offers
        theCart.handleOffers(receipt, offers, catalog);

        // Apply bundle discounts
        List<Discount> bundleDiscounts = bundleManager.calculateBundleDiscounts(
            theCart.productQuantities(), catalog);
        for (Discount discount : bundleDiscounts) {
            receipt.addDiscount(discount);
        }

        // Apply loyalty program discount only if enabled
        if (loyaltyProgramEnabled && !receipt.getItems().isEmpty()) {
            double subtotal = receipt.getTotalPrice();
            Product representativeProduct = receipt.getItems().get(0).getProduct();
            Discount loyaltyDiscount = loyaltyManager.calculateLoyaltyDiscount(subtotal, representativeProduct);
            if (loyaltyDiscount != null) {
                receipt.addDiscount(loyaltyDiscount);
            }
        }

        return receipt;
    }
}
