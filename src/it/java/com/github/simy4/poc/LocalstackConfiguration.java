package com.github.simy4.poc;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LocalstackConfiguration {
  @Bean
  @Primary
  public AWSCredentialsProvider localstackCredentialsProvider(
      @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
      @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey) {
    return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
  }

  @Bean
  @Primary
  public EndpointConfiguration localstackEndpointConfiguration(
      @Value("${spring.cloud.aws.dynamodb.endpoint}") String endpoint,
      @Value("${spring.cloud.aws.region.static}") String region) {
    return new EndpointConfiguration(endpoint, region);
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
