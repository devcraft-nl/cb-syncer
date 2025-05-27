package nl.devcraft.cb.persist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import nl.devcraft.cb.onix.ParsedBook;

@ApplicationScoped
public class BookPersister {

  @Transactional
  public void persist(ParsedBook book) {
    mapToEntity(book).persist();
  }

  private Book mapToEntity(ParsedBook parsedBook) {
    var book = new Book();
    book.isbn = parsedBook.isbn();
    book.title = parsedBook.title();
    book.author = parsedBook.author();
    return book;
  }

}
