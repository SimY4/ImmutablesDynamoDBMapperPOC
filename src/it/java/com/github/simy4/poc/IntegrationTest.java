package com.github.simy4.poc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTest {
  @Container
  static final LocalStackContainer localstack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0.1"))
          .withServices(LocalStackContainer.Service.DYNAMODB);

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
