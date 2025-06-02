package nl.devcraft.cb.persist;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import nl.devcraft.cb.onix.ParsedBook;

@ApplicationScoped
public class BookService {

  @Transactional
  public void save(ParsedBook book) {
    var bookEntity = mapToBookEntity(book);
    bookEntity.authors.forEach(author -> author.persist());
    bookEntity.persist();
    mapToBookUpdateEntity(book).persist();
  }

  @Transactional
  public void update(ParsedBook book) {
    var bookEntity = Book.findByIsbn(book.isbn());
    bookEntity.authors.forEach(author -> author.persist());
    bookEntity.persist();
    mapToBookUpdateEntity(book).persist();
  }

  private Book updateBookEntity(Book book, ParsedBook parsedBook) {
    book.title = parsedBook.title() != null ? parsedBook.title() : book.title;
    if( parsedBook.authors() != null || !parsedBook.authors().isEmpty()) {
      book.authors = parsedBook.authors().stream()
          .map(this::mapToAuthorEntity)
          .toList();
    }
    if( parsedBook.shortDescription() != null) {
      book.description = parsedBook.shortDescription();
    }
    if( parsedBook.bookImage() != null) {
      book.coverImage = parsedBook.bookImage();
    }
    if (parsedBook.productAvailability() != null) {
      book.productAvailability = parsedBook.productAvailability();
    }
    if( parsedBook.priceNoTax() != null) {
      book.priceNoTax = parsedBook.priceNoTax();
    }
    if( parsedBook.priceTax() != null) {
      book.priceTax = parsedBook.priceTax();
    }
    return book;
  }

  private Book mapToBookEntity(ParsedBook parsedBook) {
    var book = new Book();
    book.isbn = parsedBook.isbn();
    book.title = parsedBook.title();
    book.authors = parsedBook.authors().stream()
        .map(this::mapToAuthorEntity)
        .toList();
    book.description = parsedBook.shortDescription();
    book.coverImage = parsedBook.bookImage();
    book.priceNoTax = parsedBook.priceNoTax();
    book.priceTax = parsedBook.priceTax();
    book.productAvailability = parsedBook.productAvailability();
    return book;
  }

  private Author mapToAuthorEntity(String name) {
    var author = new Author();
    author.name = name;
    return author;
  }

  private BookUpdate mapToBookUpdateEntity(ParsedBook parsedBook) {
    var book = new BookUpdate();
    book.isbn = parsedBook.isbn();
    return book;
  }
}
