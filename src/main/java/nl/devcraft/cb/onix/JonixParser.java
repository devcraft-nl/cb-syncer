package nl.devcraft.cb.onix;

import com.tectonica.jonix.Jonix;
import com.tectonica.jonix.common.OnixVersion;
import com.tectonica.jonix.common.codelist.ContributorRoles;
import com.tectonica.jonix.common.codelist.PriceTypes;
import com.tectonica.jonix.common.codelist.ProductIdentifierTypes;
import com.tectonica.jonix.common.codelist.ResourceContentTypes;
import com.tectonica.jonix.common.codelist.ResourceForms;
import com.tectonica.jonix.common.codelist.TextTypes;
import com.tectonica.jonix.common.codelist.TitleTypes;
import com.tectonica.jonix.unify.base.BasePrice;
import com.tectonica.jonix.unify.base.BaseProduct;
import com.tectonica.jonix.util.JonixUtil;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class JonixParser {

  Set<PriceTypes> requestedPrices = JonixUtil.setOf(
      PriceTypes.RRP_including_tax,
      PriceTypes.RRP_excluding_tax
  );

  public List<ParsedBook> read(File xmlFile) {
    return Jonix.source(xmlFile, "*.xml", false)
        .onSourceStart(src -> {
          // safeguard: we skip non-ONIX-3 files
          if (src.onixVersion() != OnixVersion.ONIX3) {
            src.skipSource();
          }
        })
        .onSourceEnd(src -> {
          System.out.printf("<< Processed %d products from %s %n", src.productCount(), src.sourceName());
        })
        .stream() // iterate over the products contained in all ONIX sources
        .map(record -> {
          BaseProduct product = Jonix.toBaseProduct(record);
          String ref = product.info.recordReference;
          String isbn13 = product.info.findProductId(ProductIdentifierTypes.ISBN_13);
          String title = product.titles.findTitleText(TitleTypes.Distinctive_title_book);
          List<BasePrice> prices = product.supplyDetails.findPrices(requestedPrices);
          List<String> authors = product.contributors.getDisplayNames(ContributorRoles.By_author);
          var shortDescription = product.texts.findText(TextTypes.Short_description_annotation);
          var description = product.texts.findText(TextTypes.Description);

          var product3 = Jonix.toProduct3(record);

          var bookImage = product3.collateralDetail().supportingResources()
              .filter(sr -> sr.resourceContentType().value == ResourceContentTypes.Front_cover)
              .firstOrEmpty()
              .resourceVersions()
              .filter(rv -> rv.resourceForm().value == ResourceForms.Downloadable_file)
              .first()
              .map(rv -> rv.resourceLinks().firstValueOrNull())
              .orElse(null);

          var productAvailability =
              product3.productSupplys()
                  .firstOrEmpty()
                  .supplyDetails()
                  .firstOrEmpty()
                  .productAvailability()
                  .value()
                  .map(Enum::name)
                  .map(String::toLowerCase)
                  .orElse("unknown");

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
              .priceTax(getPrice(prices, PriceTypes.RRP_including_tax))
              .build();
        })
        .toList();
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
