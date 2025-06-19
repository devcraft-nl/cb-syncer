package nl.devcraft.cb.unzip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import nl.devcraft.cb.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UnzipperTest {

  public static final String UNZIP_DIR = "src/test/resources/unzip";
  public static final String ZIP_DIR = "src/test/resources/zip";

  @AfterEach
  void tearDown() throws IOException {
    TestUtil.removeFilesFromDir(Path.of(UNZIP_DIR));
  }

  Unzipper unzipper = new Unzipper();

  @Test
  void it_can_unzip_files_in_a_directory() {
    unzipper.unzipDir(ZIP_DIR, UNZIP_DIR);
    File unzipDirFile = Path.of(UNZIP_DIR).toFile();

    List<String> files = Stream.of(unzipDirFile.listFiles())
        .map(File::getName)
        .toList();
    assertEquals(1, files.size());
    assertThat(files).containsExactlyInAnyOrder("Onix3sample_refnames.onx");
  }

}