package nl.devcraft.cb;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import nl.devcraft.cb.resourcemanager.WithDBServer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@QuarkusMainTest
@WithDBServer
public class ParseCommandTest {

  @Test
  public void it_should_parse_an_onyx_file_and_persist_the_data(QuarkusMainLauncher launcher) {
    LaunchResult result = launcher.launch("parse", "-d=src/test/resources/onyx/");
    assertThat(result.exitCode()).isEqualTo(0);
    assertThat(result.getOutput()).contains("Stored book with title: Roseanna");
  }




}