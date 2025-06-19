package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cb_books")
public class Book extends PanacheEntity {

  @Column(unique = true)
  public Long isbn;

  @Column(length = 700)
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

  @CreationTimestamp
  public Instant created;

  @UpdateTimestamp
  private Instant modified;

  @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
  public List<Author> authors = new ArrayList<>();

  public static Optional<Book> findByIsbn(Long isbn) {
    return find("isbn", isbn).firstResultOptional();
  }

}
