package nl.devcraft.cb.resourcemanager;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;
import org.testcontainers.utility.MountableFile;

public class FtpServerResourceManager
    implements QuarkusTestResourceConfigurableLifecycleManager<WithFTPServer>,
    DevServicesContext.ContextAware {

  public static final String DEFAULT_USER = "test";
  public static final String DEFAULT_PASSWORD = "test";
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 21;
  private static final int PASSIVE_MODE_PORT = 21000;

  private Optional<String> containerNetworkId;
  private GenericContainer container;

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    containerNetworkId = context.containerNetworkId();
  }

  @Override
  public Map<String, String> start() {

    container = new FixedHostPortGenericContainer<>(
        "delfer/alpine-ftp-server:latest")
        .withFixedExposedPort(PASSIVE_MODE_PORT, PASSIVE_MODE_PORT)
        .withExposedPorts(DEFAULT_PORT)
        .withEnv("USERS", DEFAULT_USER + "|" + DEFAULT_PASSWORD)
        .withEnv("MIN_PORT", String.valueOf(PASSIVE_MODE_PORT))
        .withEnv("MAX_PORT", String.valueOf(PASSIVE_MODE_PORT))
    //
    // container = new FixedHostPortGenericContainer(
    //     new ImageFromDockerfile()
    //         .withDockerfileFromBuilder(builder ->
    //             builder
    //                 .from("delfer/alpine-ftp-server:latest")
    //                 .build()
    //         )
    // )
    //     .withExposedPorts(DEFAULT_PORT)
    //     .withEnv("USERS", DEFAULT_USER + "|" + DEFAULT_PASSWORD)// + "|/ftp/test|10000")
        .withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(5)))
        .withCopyFileToContainer(
            MountableFile.forClasspathResource("/onix_refnames/Onix3sample_refnames.xml"),
            "/ftp/test/Onix3sample_refnames.xml"
        )
    ;

    // apply the network to the container
    containerNetworkId.ifPresent(container::withNetworkMode);
    // start container before retrieving its URL or other properties
    container.start();

    // return a map containing the configuration the application needs to use the service
    return ImmutableMap.of(
        "ftp.user", DEFAULT_USER,
        "ftp.password", DEFAULT_PASSWORD,
        "ftp.port", String.valueOf(container.getMappedPort(DEFAULT_PORT)),
        "ftp.host", DEFAULT_HOST);
  }

  @Override
  public void init(WithFTPServer annotation) {
    QuarkusTestResourceConfigurableLifecycleManager.super.init(annotation);
  }

  @Override
  public void stop() {
    if (container != null && container.isRunning()) {
      container.stop();
    }
  }
}
