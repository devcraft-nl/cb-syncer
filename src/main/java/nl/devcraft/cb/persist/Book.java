package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cb_books")
public class Book extends PanacheEntity {

  @Column(unique = true)
  public Long isbn;

  public String title;

  public String coverImage;

  @Column(length = 2000)
  public String description;

  public String shortDescription;

  public Double priceNoTax;

  public Double priceTax;

  public String currency;

  public String productAvailability;

  public String language;

  public String publisher;

  public String ref;

  @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
  public List<Author> authors = new ArrayList<>();

  public static Book findByIsbn(Long isbn) {
    return find("isbn", isbn).firstResult();
  }

}
