package nl.devcraft.cb.onix;

import com.tectonica.jonix.Jonix;
import com.tectonica.jonix.JonixRecord;
import com.tectonica.jonix.JonixSource;
import com.tectonica.jonix.common.ListOfOnixComposite;
import com.tectonica.jonix.common.OnixElement;
import com.tectonica.jonix.common.OnixVersion;
import com.tectonica.jonix.common.codelist.ContributorRoles;
import com.tectonica.jonix.common.codelist.PriceTypes;
import com.tectonica.jonix.common.codelist.ProductIdentifierTypes;
import com.tectonica.jonix.common.codelist.ResourceContentTypes;
import com.tectonica.jonix.common.codelist.ResourceForms;
import com.tectonica.jonix.common.codelist.TextTypes;
import com.tectonica.jonix.common.codelist.TitleTypes;
import com.tectonica.jonix.onix3.Product;
import com.tectonica.jonix.onix3.ProductSupply;
import com.tectonica.jonix.unify.base.BasePrice;
import com.tectonica.jonix.util.JonixUtil;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class JonixParser {

  Set<PriceTypes> requestedPrices = JonixUtil.setOf(
      PriceTypes.FRP_including_tax,
      PriceTypes.RRP_excluding_tax
  );

  public List<ParsedBook> read(File xmlFile) {
    List<File> orderedFiles = Arrays.stream(xmlFile.listFiles())
        .filter(file -> file.getName().endsWith(".onx") && !file.isDirectory())
        .sorted()
        .toList();

    return Jonix.source(orderedFiles)
        .onSourceStart(src -> {
          // safeguard: we skip non-ONIX-3 files
          if (src.onixVersion() != OnixVersion.ONIX3) {
            src.skipSource();
          }
        })
        .onSourceEnd(src -> {
          System.out.printf("<< Processed %d products from %s %n", src.productCount(), src.sourceName());
          renameToProcessed(src);
        })
        .stream() // iterate over the products contained in all ONIX sources
        .map(this::mapToParsedBook)
        .toList();
  }

  private static void renameToProcessed(JonixSource src) {
    var fileProcessed = Path.of(src.sourceName() + ".processed");
    if (!Path.of(src.sourceName()).toFile().renameTo(fileProcessed.toFile())) {
      throw new RuntimeException("Could not rename " + src.sourceName() + " to " + fileProcessed);
    }
  }

  private ParsedBook mapToParsedBook(JonixRecord record) {
    var product = Jonix.toBaseProduct(record);
    var ref = product.info.recordReference;
    var isbn13 = product.info.findProductId(ProductIdentifierTypes.ISBN_13);
    if (isbn13 == null) {
      return null;
    }

    var title = product.titles.findTitleText(TitleTypes.Distinctive_title_book);
    List<BasePrice> prices = product.supplyDetails.findPrices(requestedPrices);
    List<String> authors = product.contributors.getDisplayNames(ContributorRoles.By_author);
    var shortDescription = product.texts.findText(TextTypes.Short_description_annotation);
    var description = product.texts.findText(TextTypes.Description);

    var product3 = Jonix.toProduct3(record);
    var bookImage = getBookImage(product3);
    var productAvailability = getProductAvailability(product3);

    return ParsedBookBuilder.builder()
        .bookImage(bookImage)
        .authors(authors)
        .ref(ref)
        .isbn(Long.valueOf(isbn13))
        .title(title)
        .description(description != null ? description.text : null)
        .shortDescription(shortDescription != null ? shortDescription.text : null)
        .productAvailability(productAvailability)
        .priceNoTax(getPrice(prices, PriceTypes.RRP_excluding_tax))
        .priceTax(getPrice(prices, PriceTypes.FRP_including_tax))
        .build();
  }

  private static String getProductAvailability(Product product3) {
    return product3.productSupplys()
        .first()
        .map(ProductSupply::supplyDetails)
        .map(ListOfOnixComposite::first)
        .filter(Optional::isPresent)
        .map(s -> s.get().productAvailability())
        .map(OnixElement::value)
        .filter(Optional::isPresent)
        .map(v -> v.get().name())
        .map(String::toLowerCase)
        .orElse("unknown");
  }

  private static String getBookImage(Product product3) {
    return product3.collateralDetail().supportingResources()
        .first()
        .filter(sr -> sr.resourceContentType().value == ResourceContentTypes.Front_cover)
        .map(sr -> sr.resourceVersions().first())
        .filter(Optional::isPresent)
        .filter(rv -> rv.get().resourceForm().value == ResourceForms.Downloadable_file)
        .map(rv -> rv.get().resourceLinks().firstValueOrNull())
        .orElse(null);
  }

  private static Double getPrice(List<BasePrice> prices, PriceTypes priceType) {
    return prices.stream()
        .filter(p -> p.priceType == priceType)
        .filter(p -> p.currencyCode.getCode().equals("EUR"))
        .map(p -> p.priceAmount)
        .findFirst()
        .orElse(null);
  }
}
