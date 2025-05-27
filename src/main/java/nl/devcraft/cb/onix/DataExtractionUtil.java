package nl.devcraft.cb.onix;

import java.util.function.Function;
import java.util.function.Supplier;
import org.editeur.ns.onix._3_0.reference.Product;
import org.editeur.ns.onix._3_0.reference.TitleWithoutPrefix;

public class DataExtractionUtil {

  private DataExtractionUtil() {}

  public static String title(Product product) {
    var titelRaw = product.getDescriptiveDetail().getTitleDetail().getFirst()
        .getTitleElement()
        .getLast()
        .getContent()
        .getLast();
    return ((TitleWithoutPrefix)titelRaw).getValue();
  }

  public static String isbn(Product product) {
    return product.getProductIdentifier().getFirst().getIDValue().getValue();
  }


}
