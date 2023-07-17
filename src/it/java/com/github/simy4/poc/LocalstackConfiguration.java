package com.github.simy4.poc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.net.URI;

@Configuration
public class LocalstackConfiguration {
  @Bean
  @Primary
  public AwsCredentialsProvider localstackCredentialsProvider(
      @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
      @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }

  @Primary
  @Bean(destroyMethod = "close")
  public DynamoDbAsyncClient localStackDynamoDB(
      AwsCredentialsProvider credentialsProvider,
      @Value("${spring.cloud.aws.dynamodb.endpoint}") URI endpoint,
      @Value("${spring.cloud.aws.region.static}") String region) {
    return DynamoDbAsyncClient.builder()
        .credentialsProvider(credentialsProvider)
        .endpointOverride(endpoint)
        .region(Region.of(region))
        .build();
  }
}
