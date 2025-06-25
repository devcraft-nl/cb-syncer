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
public class UpdateCommandTest {

  public static final String ONIX_REFNAMES_BLOCKUPDATE = "src/test/resources/onix_refnames_blockupdate/";

  @AfterEach
  void tearDown() throws IOException {
    TestUtil.renameFilesFromProcesed(Path.of(ONIX_REFNAMES_BLOCKUPDATE));
  }

  @Test
  public void it_should_parse_an_onix_update_file_and_persist_the_data(QuarkusMainLauncher launcher) {
    LaunchResult result = launcher.launch("update", "-d=" + ONIX_REFNAMES_BLOCKUPDATE);
    assertThat(result.exitCode()).isEqualTo(0);
    assertThat(result.getOutput()).contains("Updated book with isbn: 9780007232833");
    assertThat(Path.of(ONIX_REFNAMES_BLOCKUPDATE, "Onix3sample_refnames_blockupdate.onx")).doesNotExist();
    assertThat(Path.of(ONIX_REFNAMES_BLOCKUPDATE, "Onix3sample_refnames_blockupdate.onx.processed")).exists();
  }

}