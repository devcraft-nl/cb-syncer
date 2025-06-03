package nl.devcraft.cb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestUtil {

  public static final String POST_PROCESSED = ".processed";

  public static void renameFilesFromProcesed(Path dir) throws IOException {
    try (Stream<Path> walk = Files.walk(dir)) {
      walk.filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith(POST_PROCESSED))
          .forEach(path -> path.toFile()
              .renameTo(fileRenamedBack(path)));
    }
  }

  private static File fileRenamedBack(Path path) {
    var renamedFile = path.toFile().getPath().replace(POST_PROCESSED, "");
    return new File(renamedFile);
  }


}
