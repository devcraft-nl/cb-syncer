package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cb_author")
public class Author extends PanacheEntity {

  String name;

  @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
  Book book;

}
