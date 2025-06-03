package nl.devcraft.cb.onix;

import io.soabase.recordbuilder.core.RecordBuilder;
import java.util.List;

@RecordBuilder
public record ParsedBook(String ref, String bookImage, List<String> authors, Long isbn, String title,
                         String shortDescription,
                         String description,
                         String productAvailability,
                         Double priceNoTax, Double priceTax) {
}
