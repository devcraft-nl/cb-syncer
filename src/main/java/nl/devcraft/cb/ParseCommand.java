package nl.devcraft.cb;

import java.nio.file.Paths;
import nl.devcraft.cb.onix.JonixParser;
import nl.devcraft.cb.persist.BookService;
import picocli.CommandLine;

@CommandLine.Command(name = "parse", description = "parse the files in the onyx dir")
class ParseCommand implements Runnable {

  private final BookService persister;
  private final JonixParser jonixParser;

  public ParseCommand(JonixParser jonixParser, BookService persister) {
    this.jonixParser = jonixParser;
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
    jonixParser.read(Paths.get(dir).toFile())
        .forEach(book -> {
          persister.save(book);
          System.out.println("Stored book with title: " + book.title());
        });
  }
}