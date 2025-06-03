package nl.devcraft.cb.persist;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import nl.devcraft.cb.onix.ParsedBookBuilder;
import nl.devcraft.cb.resourcemanager.MySQLClient;
import nl.devcraft.cb.resourcemanager.WithDBClient;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@WithDBServer
class BookServiceTest {

  @Inject
  BookService bookService;

  @WithDBClient
  MySQLClient mysqlClient;

  @AfterEach
  void tearDown() throws SQLException {
    mysqlClient.execute("truncate table cb_author; truncate table cb_books;");
  }

  @Test
  void it_should_save_a_book_in_the_database() throws SQLException {
    var parsedBook = ParsedBookBuilder.builder()
        .isbn(123456L)
        .title("title")
                .shortDescription("shortDescription")
                    .description("description")
        .authors(List.of("author1", "author2"))
        .build();
    bookService.save(parsedBook);
    try (var conn = mysqlClient.connection()) {

      var result = conn.createStatement().executeQuery("select * from cb_books");
      result.next();
      assertThat(result.getString("title")).isEqualTo("title");
      assertThat(result.getString("shortDescription")).isEqualTo("shortDescription");
      assertThat(result.getString("description")).isEqualTo("description");
    }

  }

  @Test
  void it_should_update_a_book_in_the_database() throws SQLException {
    var parsedBook = ParsedBookBuilder.builder()
        .isbn(123456L)
        .title("title")
        .shortDescription("shortDescription")
        .description("description")
        .authors(List.of("author1", "author2"))
        .build();
    bookService.save(parsedBook);

    var updatedBook = ParsedBookBuilder.builder(parsedBook)
        .title("updatedTitle")
        .build();

    bookService.update(updatedBook);
    try (var conn = mysqlClient.connection()) {

      var result = conn.createStatement().executeQuery("select * from cb_books");
      result.next();
      assertThat(result.getString("title")).isEqualTo("updatedTitle");
      assertThat(result.next()).isFalse();
    }

  }

}