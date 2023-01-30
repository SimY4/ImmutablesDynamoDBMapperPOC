package com.github.simy4.poc;

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
    return new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.3.1"))
        .withServices(Service.DYNAMODB);
  }

  @Bean
  @Primary
  public AmazonDynamoDBAsync localStackDynamoDB(LocalStackContainer localStackContainer) {
    return AmazonDynamoDBAsyncClientBuilder.standard()
        .withCredentials(
            new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                    localStackContainer.getAccessKey(), localStackContainer.getSecretKey())))
        .withEndpointConfiguration(
            new EndpointConfiguration(
                localStackContainer.getEndpointOverride(Service.DYNAMODB).toString(),
                localStackContainer.getRegion()))
        .build();
  }
}