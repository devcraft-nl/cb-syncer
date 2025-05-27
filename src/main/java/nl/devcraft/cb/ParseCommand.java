package nl.devcraft.cb;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import nl.devcraft.cb.onix.OnixParser;
import nl.devcraft.cb.onix.ParsedBook;
import nl.devcraft.cb.persist.Book;
import nl.devcraft.cb.persist.BookPersister;
import picocli.CommandLine;

@CommandLine.Command(name = "parse", description = "parse the files in the onyx dir")
class ParseCommand implements Runnable {

  private final OnixParser onyxParser;
  private final BookPersister persister;

  public ParseCommand(OnixParser onyxParser, BookPersister persister) {
    this.onyxParser = onyxParser;
    this.persister = persister;
  }

  private String dir;

  @CommandLine.Option(names = "-d", defaultValue = "onyx/", description = "Directory to parse")
  public void setDir(String dir) {
    this.dir = dir;
  }

  @Override
  public void run() {
    System.out.println(dir);
    try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
      paths
          .filter(Files::isRegularFile)
          .forEach(file -> {
            onyxParser.unmarshal(file.toFile())
                .forEach(book -> {
                  persister.persist(book);
                  System.out.println("Stored book with title: " + book.title());
                });
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}