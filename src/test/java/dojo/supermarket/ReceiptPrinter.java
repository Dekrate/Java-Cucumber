package dojo.supermarket;

import dojo.supermarket.model.Discount;
import dojo.supermarket.model.ProductUnit;
import dojo.supermarket.model.Receipt;
import dojo.supermarket.model.ReceiptItem;

import java.math.BigDecimal;
import java.util.Locale;

public class ReceiptPrinter {

    private final int columns;

    public ReceiptPrinter() {
        this(40);
    }

    public ReceiptPrinter(final int columns) {
        this.columns = columns;
    }

    public String printReceipt(final Receipt receipt) {
        final StringBuilder result = new StringBuilder();
        for (ReceiptItem item : receipt.getItems()) {
            final String receiptItem = presentReceiptItem(item);
            result.append(receiptItem);
        }
        for (Discount discount : receipt.getDiscounts()) {
            final String discountPresentation = presentDiscount(discount);
            result.append(discountPresentation);
        }

        result.append("\n");
        result.append(presentTotal(receipt));
        return result.toString();
    }

    private String presentReceiptItem(final ReceiptItem item) {
        final String totalPricePresentation =
                presentPrice(item.totalPrice());
        final String name = item.product().name();

        String line = formatLineWithWhitespace(name, totalPricePresentation);

        if (item.quantity().compareTo(BigDecimal.ONE) != 0) {
            line += "  " + presentPrice(item.price()) + " * "
                    + presentQuantity(item) + "\n";
        }
        return line;
    }

    private String presentDiscount(final Discount discount) {
        final String name = discount.description()
                + "(" + discount.product().name() + ")";
        final String value = presentPrice(discount.discountAmount());

        return formatLineWithWhitespace(name, value);
    }

    private String presentTotal(final Receipt receipt) {
        final String name = "Total: ";
        final String value = presentPrice(receipt.getTotalPrice());
        return formatLineWithWhitespace(name, value);
    }

    private String formatLineWithWhitespace(final String name,
                                            final String value) {
        final StringBuilder line = new StringBuilder();
        line.append(name);
        final int whitespaceSize = this.columns - name.length()
                - value.length();
        for (int i = 0; i < whitespaceSize; i++) {
            line.append(" ");
        }
        line.append(value);
        line.append('\n');
        return line.toString();
    }

    private static String presentPrice(final BigDecimal price) {
        return String.format(Locale.UK, "%.2f", price);
    }

    private static String presentQuantity(final ReceiptItem item) {
        return ProductUnit.EACH.equals(item.product().unit())
                ? String.format("%d", item.quantity().intValue())
                : String.format(Locale.UK, "%.3f", item.quantity());
    }
}

