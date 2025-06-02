package nl.devcraft.cb.onix;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Objects;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@WithDBServer
class JonixParserTest {

  @Inject
  JonixParser parser;

  @Test
  void it_should_parse_the_onix_sample_reference_file() {
    assertNotNull(parser);
    var res = Objects.requireNonNull(JonixParserTest.class.getResource("/onix_refnames/"));
    List<ParsedBook> books = parser.read(new File(res.getFile()));

    assertThat(books).isNotNull();
    assertThat(books)
        .hasSize(1)
        .extracting(
            ParsedBook::isbn,
            ParsedBook::title,
            ParsedBook::authors,
            ParsedBook::shortDescription,
            ParsedBook::priceTax,
            ParsedBook::priceNoTax,
            ParsedBook::bookImage,
            ParsedBook::productAvailability,
            ParsedBook::ref
            )
        .containsOnly(new Tuple(
            "9780007232833",
            "Roseanna",
            List.of("Maj Sjöwall", "Per Wahlöö"),
            null,
            7.99,
            8.99,
            "http://www.harpercollins.co.uk/covers/9780007232833.jpg",
            "in_stock",
            "com.globalbookinfo.onix.01734529"
            ));
  }

  @Test
  void it_should_parse_the_onix_sample_reference_block_file() {
    assertNotNull(parser);
    var res = Objects.requireNonNull(JonixParserTest.class.getResource(
        "/onix_refnames_blockupdate/"));
    List<ParsedBook> books = parser.read(new File(res.getFile()));

    assertThat(books).isNotNull();
    assertThat(books)
        .hasSize(1)
        .extracting(
            ParsedBook::isbn,
            ParsedBook::title,
            ParsedBook::authors,
            ParsedBook::shortDescription,
            ParsedBook::priceTax,
            ParsedBook::priceNoTax,
            ParsedBook::bookImage,
            ParsedBook::productAvailability,
            ParsedBook::ref
        )
        .containsOnly(new Tuple(
            "9780007232833",
            null,
            List.of(),
            null,
            8.99,
            9.99,
            null,
            "in_stock",
            "com.globalbookinfo.onix.01734529"
        ));
  }


}