package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cb_author")
public class Author extends PanacheEntity {

  String name;

}
