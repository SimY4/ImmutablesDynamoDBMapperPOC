package com.github.simy4.poc;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class LocalstackConfiguration {
  @Bean(initMethod = "start", destroyMethod = "stop")
  public LocalStackContainer localStackContainer() {
    return new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.1.0"))
        .withServices(Service.DYNAMODB);
  }

  @Bean
  @Primary
  public AWSCredentialsProvider localstackCredentialsProvider(
      LocalStackContainer localStackContainer) {
    return new AWSStaticCredentialsProvider(
        new BasicAWSCredentials(
            localStackContainer.getAccessKey(), localStackContainer.getSecretKey()));
  }

  @Bean
  @Primary
  public EndpointConfiguration localstackEndpointConfiguration(
      LocalStackContainer localStackContainer) {
    return new EndpointConfiguration(
        localStackContainer.getEndpointOverride(Service.DYNAMODB).toString(),
        localStackContainer.getRegion());
  }

  @Primary
  @Bean(destroyMethod = "shutdown")
  public AmazonDynamoDBAsync localStackDynamoDB(
      AWSCredentialsProvider credentialsProvider, EndpointConfiguration endpointConfiguration) {
    return AmazonDynamoDBAsyncClientBuilder.standard()
        .withCredentials(credentialsProvider)
        .withEndpointConfiguration(endpointConfiguration)
        .build();
  }
}
