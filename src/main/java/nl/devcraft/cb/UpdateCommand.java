package nl.devcraft.cb;

import java.nio.file.Paths;
import nl.devcraft.cb.onix.JonixParser;
import nl.devcraft.cb.persist.BookService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "parse the blockfiles in the onyx dir")
class UpdateCommand implements Runnable {

  private final BookService persister;
  private final JonixParser jonixParser;

  public UpdateCommand(JonixParser jonixParser, BookService persister) {
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
          persister.update(book);
          System.out.println("Updated book with title: " + book.title());
        });
  }
}