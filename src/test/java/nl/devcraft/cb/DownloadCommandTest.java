package nl.devcraft.cb;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import nl.devcraft.cb.resourcemanager.WithFTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@QuarkusMainTest
@WithFTPServer
@WithDBServer
class DownloadCommandTest {

  public static final String DOWNLOAD_RESOURCE_MAP = "src/test/resources/download";
  public static final String UNZIP_RESOURCE_MAP = "src/test/resources/unzip";

  @Test
  public void it_should_download_onix_files_from_ftp(QuarkusMainLauncher launcher) {
    LaunchResult result =
        launcher.launch("download", "-i=/ftp/test", "-o=" + DOWNLOAD_RESOURCE_MAP, "-u=" + UNZIP_RESOURCE_MAP);

    assertThat(result.exitCode()).isEqualTo(0);
    var expectedDownloadedFile = Path.of(DOWNLOAD_RESOURCE_MAP, "Onix3sample_refnames.zip").toFile();
    assertThat(expectedDownloadedFile.exists()).isTrue();
    var expectedUnzippedFile = Path.of(UNZIP_RESOURCE_MAP, "Onix3sample_refnames.onx").toFile();
    assertThat(expectedUnzippedFile.exists()).isTrue();
  }

  @AfterEach
  void cleanup() throws IOException {
    TestUtil.removeFilesFromDir(Path.of(DOWNLOAD_RESOURCE_MAP));
    TestUtil.removeFilesFromDir(Path.of(UNZIP_RESOURCE_MAP));
  }

}