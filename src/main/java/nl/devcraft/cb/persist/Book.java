package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cb_books")
public class Book extends PanacheEntityBase {

  @Id
  public String isbn;

  public String title;

  public String author;


}
