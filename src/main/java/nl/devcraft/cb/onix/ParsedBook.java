package nl.devcraft.cb.onix;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.List;

@RecordBuilder
public record ParsedBook(String ref, String bookImage, List<String> authors, String isbn, String title,
                         String shortDescription,
                         String productAvailability,
                         Double priceNoTax, Double priceTax) {
}
