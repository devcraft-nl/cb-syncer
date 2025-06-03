package nl.devcraft.cb.persist;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import nl.devcraft.cb.onix.ParsedBook;

@ApplicationScoped
public class BookService {

  @Transactional
  public void save(ParsedBook book) {
    var bookEntity = mapToBookEntity(book);
    bookEntity.persist();
    bookEntity.authors.forEach(a -> a.persist());
  }

  @Transactional
  public void update(ParsedBook book) {
    var bookEntity = Book.findByIsbn(book.isbn());
    Book updatedBookEntity;
    if( bookEntity == null ) {
      updatedBookEntity = mapToBookEntity(book);
    } else {
      updatedBookEntity = updateBookEntity(bookEntity, book);
    }
    updatedBookEntity.persist();
    updatedBookEntity.authors.forEach(a -> a.persist());
    //save isbn number to update table
    mapToBookUpdateEntity(book).persist();
  }

  private Book updateBookEntity(Book book, ParsedBook parsedBook) {
    book.title = parsedBook.title() != null ? parsedBook.title() : book.title;
    if (parsedBook.authors() != null && !parsedBook.authors().isEmpty()) {
      book.authors.forEach(PanacheEntityBase::delete);
      book.authors = parsedBook.authors().stream()
          .map(a -> mapToAuthorEntity(a, book))
          .toList();
    }
    if (parsedBook.shortDescription() != null) {
      book.shortDescription = parsedBook.shortDescription();
    }
    if (parsedBook.description() != null) {
      book.description = parsedBook.description();
    }

    if (parsedBook.bookImage() != null) {
      book.coverImage = parsedBook.bookImage();
    }
    if (parsedBook.productAvailability() != null) {
      book.productAvailability = parsedBook.productAvailability();
    }
    if (parsedBook.priceNoTax() != null) {
      book.priceNoTax = parsedBook.priceNoTax();
    }
    if (parsedBook.priceTax() != null) {
      book.priceTax = parsedBook.priceTax();
    }
    return book;
  }

  private Book mapToBookEntity(ParsedBook parsedBook) {
    var bookEntity = new Book();
    bookEntity.isbn = parsedBook.isbn();
    bookEntity.title = parsedBook.title();
    bookEntity.authors = parsedBook.authors()
        .stream()
        .map(a -> mapToAuthorEntity(a, bookEntity))
        .toList();
    bookEntity.shortDescription = parsedBook.shortDescription();
    bookEntity.description = parsedBook.description();
    bookEntity.coverImage = parsedBook.bookImage();
    bookEntity.priceNoTax = parsedBook.priceNoTax();
    bookEntity.priceTax = parsedBook.priceTax();
    bookEntity.productAvailability = parsedBook.productAvailability();
    return bookEntity;
  }

  private Author mapToAuthorEntity(String name, Book book) {
    var author = new Author();
    author.name = name;
    author.book = book;
    return author;
  }

  private BookUpdate mapToBookUpdateEntity(ParsedBook parsedBook) {
    var book = new BookUpdate();
    book.isbn = parsedBook.isbn();
    return book;
  }
}
