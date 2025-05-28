package nl.devcraft.cb;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nl.devcraft.cb.resourcemanager.WithFTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@QuarkusMainTest
@WithFTPServer
class DownloadCommandTest {

  @Test
  public void it_should_download_onyx_files_from_ftp(QuarkusMainLauncher launcher) {
    LaunchResult result = launcher.launch("download", "-i=/ftp/test", "-o=src/test/resources/download");

    assertThat(result.exitCode()).isEqualTo(0);
    var expectedDownloadedFile = Path.of("src/test/resources/download", "Onix3sample_refnames.xml").toFile();
    assertThat(expectedDownloadedFile.exists()).isTrue();
  }

  @AfterEach
  void cleanup() throws IOException {
    Files.walk(Path.of("src/test/resources/download"), 1)
        .filter(Files::isRegularFile)
        .forEach(file -> file.toFile().delete());
  }

}