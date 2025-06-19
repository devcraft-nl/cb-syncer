package nl.devcraft.cb;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import java.io.IOException;
import java.nio.file.Path;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@QuarkusMainTest
@WithDBServer
public class ParseCommandTest {

  public static final String ONIX_REFNAMES = "src/test/resources/onix_refnames/";

  @AfterEach
  void tearDown() throws IOException {
    TestUtil.renameFilesFromProcesed(Path.of(ONIX_REFNAMES));
  }

  @Test
  public void it_should_parse_an_onyx_file_and_persist_the_data(QuarkusMainLauncher launcher) {
    LaunchResult result = launcher.launch("parse", "-d=" + ONIX_REFNAMES);
    assertThat(result.exitCode()).isEqualTo(0);
    assertThat(result.getOutput()).contains("Stored book with title: Roseanna");
    assertThat(Path.of(ONIX_REFNAMES, "Onix3sample_refnames.onx")).doesNotExist();
    assertThat(Path.of(ONIX_REFNAMES, "Onix3sample_refnames.onx.processed")).exists();
  }

}