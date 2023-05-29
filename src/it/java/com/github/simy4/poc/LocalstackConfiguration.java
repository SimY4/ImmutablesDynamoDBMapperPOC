package com.github.simy4.poc;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Configuration
public class LocalstackConfiguration {
  @RestartScope
  @Bean(initMethod = "start", destroyMethod = "stop")
  public LocalStackContainer localStackContainer() {
    return new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
        .withServices(Service.DYNAMODB);
  }

  @Bean
  @Primary
  public AwsCredentialsProvider localstackCredentialsProvider(
      LocalStackContainer localStackContainer) {
    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(
            localStackContainer.getAccessKey(), localStackContainer.getSecretKey()));
  }

  @Primary
  @Bean(destroyMethod = "close")
  public DynamoDbAsyncClient localStackDynamoDB(
      AwsCredentialsProvider credentialsProvider, LocalStackContainer localStackContainer) {
    return DynamoDbAsyncClient.builder()
        .credentialsProvider(credentialsProvider)
        .endpointOverride(localStackContainer.getEndpointOverride(Service.DYNAMODB))
        .region(Region.of(localStackContainer.getRegion()))
        .build();
  }
}
