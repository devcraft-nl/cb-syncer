package nl.devcraft.cb.resourcemanager;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceConfigurableLifecycleManager;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

public class DatabaseServerResourceManager
    implements QuarkusTestResourceConfigurableLifecycleManager<WithDBServer>,
    DevServicesContext.ContextAware {

  public static final String DEFAULT_DATABASE_NAME = "test";
  public static final String DEFAULT_DATABASE_USER = "test";
  public static final String DEFAULT_DATABASE_PASSWORD = "test";
  private Optional<String> containerNetworkId;
  private MySQLContainer<?> container;

  @Override
  public void setIntegrationTestContext(DevServicesContext context) {
    containerNetworkId = context.containerNetworkId();
  }

  @Override
  public Map<String, String> start() {
    // start a container making sure to call withNetworkMode() with the value of containerNetworkId
    // if present
    container = new MySQLContainer<>("mysql:8.0.28");
    // container
    container.withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(5)));
    // apply the network to the container
    containerNetworkId.ifPresent(container::withNetworkMode);

    // start container before retrieving its URL or other properties
    container.start();

    // return a map containing the configuration the application needs to use the service
    return ImmutableMap.of(
        "quarkus.datasource.username", DEFAULT_DATABASE_USER,
        "quarkus.datasource.password", DEFAULT_DATABASE_PASSWORD,
        "quarkus.datasource.jdbc.url", getJdbcDatasourceUrl());
  }

  private String getJdbcDatasourceUrl() {
    String jdbcUrl = container.getJdbcUrl();
    if (containerNetworkId.isPresent()) {
      // Replace hostname + port in the provided JDBC URL with the hostname of the Docker container
      // running PostgreSQL and the listening port.
      jdbcUrl = fixJdbcUrl(jdbcUrl);
    }
    return jdbcUrl;
  }

  private String fixJdbcUrl(String jdbcUrl) {
    // Part of the JDBC URL to replace
    String hostPort =
        container.getHost() + ':' + container.getMappedPort(MySQLContainer.MYSQL_PORT);

    // Host/IP on the container network plus the unmapped port
    String networkHostPort =
        container.getCurrentContainerInfo().getConfig().getHostName()
            + ':'
            + MySQLContainer.MYSQL_PORT;

    return jdbcUrl.replace(hostPort, networkHostPort);
  }

  @Override
  public void init(WithDBServer annotation) {
    QuarkusTestResourceConfigurableLifecycleManager.super.init(annotation);
  }

  @Override
  public void inject(TestInjector testInjector) {
    testInjector.injectIntoFields(
        new MySQLClient(
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword(),
            DEFAULT_DATABASE_NAME),
        new TestInjector.AnnotatedAndMatchesType(WithDBClient.class, MySQLClient.class));
  }

  @Override
  public void stop() {
    if (container != null && container.isRunning()) {
      container.stop();
    }
  }
}
