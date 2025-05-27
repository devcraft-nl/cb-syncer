package nl.devcraft.cb.onix;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Objects;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class OnixParserTest {

  @Inject
  OnixParser parser;

  @Test
  void it_should_parse_the_onix_sample_reference_file() throws Exception {
    assertNotNull(parser);
    var res = Objects.requireNonNull(OnixParserTest.class.getResource("/ONIX_3.0_sample/Onix3sample_refnames.xml"));
    List<ParsedBook> books = parser.unmarshal(new File(res.getFile()));

    //TODO: add more book metadata
    assertThat(books).isNotNull();
    assertThat(books)
        .hasSize(1)
        .extracting(ParsedBook::isbn, ParsedBook::title, ParsedBook::author)
        .contains(new Tuple( "9780007232833", "Roseanna", null ));
  }

}