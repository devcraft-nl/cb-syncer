package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Optional;

@Entity
@Table(name = "cb_books_update")
public class BookUpdate extends PanacheEntity {

  public Long isbn;

  public static Optional<BookUpdate> findByIsbn(Long isbn) {
    return find("isbn", isbn).firstResultOptional();
  }

}
