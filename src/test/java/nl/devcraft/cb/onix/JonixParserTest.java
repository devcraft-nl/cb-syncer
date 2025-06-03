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
            ParsedBook::description,
            ParsedBook::priceTax,
            ParsedBook::priceNoTax,
            ParsedBook::bookImage,
            ParsedBook::productAvailability,
            ParsedBook::ref
            )
        .containsOnly(new Tuple(
            9780007232833L,
            "Roseanna",
            List.of("Maj Sjöwall", "Per Wahlöö"),
            "<p><strong>Perennial relaunches the first novel in the classic Martin Beck detective series from the 1960s</strong> – the novels that have inspired all crime fiction written ever since.</p>",
            "<p>Widely recognised as the among the greatest crime fiction ever written, this is the first of a series of stories that pioneered the police procedural genre. The series was translated into 35 languages, sold over 10 million copies around the world, and inspired writers from Henning Mankell to Jonathan Franzen.</p><p>Written in 1965, <em>Roseanna</em> is the work of Maj Sjöwall and Per Wahlöö – a husband and wife team from Sweden, and this volume has a new introduction to help bring their work to a new audience. The novel follows the fortunes of the detective Martin Beck, whose enigmatic and taciturn character has inspired countless other policemen in crime fiction.</p><p><em>Roseanna</em> begins on a July afternoon: the body of a young woman is dredged from a canal near Sweden’s beautiful Lake Vättern. Three months later, all that Police Inspector Martin Beck knows is that her name is Roseanna, that she came from Lincoln, Nebraska, and that she could have been strangled by any one of eighty-five people.</p><p>With its authentically rendered settings and vividly realized characters, and its command over the intricately woven details of police detection, <em>Roseanna</em> is a masterpiece of suspense and sadness.</p>",
            null,
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
            9780007232834L,
            null,
            List.of(),
            null,
            null,
            9.99,
            null,
            "in_stock",
            "com.globalbookinfo.onix.01734529"
        ));
  }


}