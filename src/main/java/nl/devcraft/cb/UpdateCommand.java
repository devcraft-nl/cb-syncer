package nl.devcraft.cb;

import java.nio.file.Paths;
import java.util.Objects;
import nl.devcraft.cb.onix.JonixParser;
import nl.devcraft.cb.onix.ParsedBook;
import nl.devcraft.cb.persist.BookService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "parse the blockfiles in the onix dir")
class UpdateCommand implements Runnable {

  private final BookService persister;
  private final JonixParser jonixParser;

  public UpdateCommand(JonixParser jonixParser, BookService persister) {
    this.jonixParser = jonixParser;
    this.persister = persister;
  }

  private String dir;

  @CommandLine.Option(names = "-d", defaultValue = "onix/", description = "Directory to parse")
  public void setDir(String dir) {
    this.dir = dir;
  }

  @Override
  public void run() {
    System.out.println(dir);
    jonixParser.read(Paths.get(dir).toFile())
        .stream()
        .filter(Objects::nonNull)
        .forEach(this::storeBook);
  }

  private void storeBook(ParsedBook book) {
    try {
      persister.update(book);
      System.out.println("Updated book with isbn: " + book.isbn());
    } catch(Exception e) {
      System.out.printf("Could not save book with isbn: %s; %s%n", book.isbn(), e.getMessage());
    }
  }
}