package nl.devcraft.cb.onix;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.editeur.ns.onix._3_0.reference.ONIXMessage;
import org.editeur.ns.onix._3_0.reference.Product;

import static nl.devcraft.cb.onix.DataExtractionUtil.isbn;
import static nl.devcraft.cb.onix.DataExtractionUtil.title;

@ApplicationScoped
public class OnixParser {

  public List<ParsedBook> unmarshal(File xmlFile) {
    JAXBContext context = null;
    try {
      context = JAXBContext.newInstance(ONIXMessage.class);
      var onixMessage = (ONIXMessage) context.createUnmarshaller()
          .unmarshal(xmlFile);
      return onixMessage.getProduct().stream()
          .map(this::parseBook)
          .toList();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  private ParsedBook parseBook(Product product) {
    var author = product.getDescriptiveDetail().getContributor().getFirst().getSourcename();
    return new ParsedBook(
        isbn(product),
        title(product),
        author);
  }

}
