package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "cb_books")
public class Book extends PanacheEntityBase {

  @Id
  public String isbn;

  public String title;

  @OneToMany
  public List<Author> authors;

  public String coverImage;

  public String description;

  public double priceNoTax;

  public double priceTax;

  public String currency;

  public String productAvailability;

  public String language;

  public String publisher;

  public String ref;

  public static Book findByIsbn(String isbn) {
    return find("isbn", isbn).firstResult();
  }

}
