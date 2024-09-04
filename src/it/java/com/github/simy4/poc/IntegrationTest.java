package com.github.simy4.poc;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {
  static final LocalStackContainer localstack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.7.1"))
          .withServices(LocalStackContainer.Service.DYNAMODB)
          .withReuse(true);

  static {
    TestcontainersConfiguration.getInstance()
        .updateUserConfig("testcontainers.reuse.enable", "true");
  }

  @BeforeAll
  static void init() {
    localstack.start();
    localstack.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("localstack")));
    Runtime.getRuntime().addShutdownHook(new Thread(localstack::stop));
  }

  @DynamicPropertySource
  static void localstackPropertySource(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
    registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);
    registry.add("spring.cloud.aws.region.static", localstack::getRegion);
    registry.add(
        "spring.cloud.aws.dynamodb.endpoint",
        () -> localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB));
  }
}
