package nl.devcraft.cb;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@QuarkusMainTest
@WithDBServer
public class UpdateCommandTest {

  @Test
  public void it_should_parse_an_onix_update_file_and_persist_the_data(QuarkusMainLauncher launcher) {
    LaunchResult result = launcher.launch("update", "-d=src/test/resources/onix_refnames_blockupdate/");
    assertThat(result.exitCode()).isEqualTo(0);
    assertThat(result.getOutput()).contains("Stored book with title: Roseanna");
  }

}