package com.github.simy4.poc;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.github.simy4.poc.model.ModifiableEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/** Starting point of this application. */
@SpringBootApplication
public class ImmutablesDynamoDbMapperPocApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImmutablesDynamoDbMapperPocApplication.class, args);
  }

  // AWS v1

  @Bean
  public AWSCredentialsProvider awsCredentialsProvider(
      @Value("${spring.cloud.aws.credentials.access-key:#{null}}") String staticAccessKey,
      @Value("${spring.cloud.aws.credentials.secret-key:#{null}}") String staticSecretKey) {
    return null == staticAccessKey || null == staticSecretKey
        ? DefaultAWSCredentialsProviderChain.getInstance()
        : new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(staticAccessKey, staticSecretKey));
  }

  @Lazy
  @Bean
  public AwsRegionProvider awsRegionProvider(
      @Value("${spring.cloud.aws.region.static:#{null}}") String staticRegion) {
    return null == staticRegion
        ? new DefaultAwsRegionProviderChain()
        : new AwsRegionProvider() {
          @Override
          public String getRegion() throws SdkClientException {
            return staticRegion;
          }
        };
  }

  @Lazy
  @Bean(destroyMethod = "shutdown")
  public AmazonDynamoDBAsync dynamoDB(
      AWSCredentialsProvider awsCredentialsProvider,
      AwsRegionProvider regionProvider,
      @Value("${spring.cloud.aws.dynamodb.endpoint:#{null}}") String endpoint) {
    AmazonDynamoDBAsyncClientBuilder builder =
        AmazonDynamoDBAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider);
    builder =
        null == endpoint
            ? builder.withRegion(regionProvider.getRegion())
            : builder.withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(endpoint, regionProvider.getRegion()));
    return builder.build();
  }

  @Bean
  public DynamoDBMapper dynamoDBMapper(AmazonDynamoDBAsync dynamoDB, Environment env) {
    var builder = DynamoDBMapperConfig.builder();
    builder.setTableNameResolver(
        (clazz, config) -> {
          var dynamoDBTable = clazz.getAnnotation(DynamoDBTable.class);
          return env.resolvePlaceholders(dynamoDBTable.tableName());
        });
    return new DynamoDBMapper(dynamoDB, builder.build());
  }

  @Bean
  @Profile("local")
  public ApplicationRunner dynamoDBInitializer(
      AmazonDynamoDBAsync dynamoDB, DynamoDBMapper dynamoDBMapper) {
    return _ -> {
      var request = dynamoDBMapper.generateCreateTableRequest(ModifiableEntity.class);
      TableUtils.createTableIfNotExists(
          dynamoDB, request.withProvisionedThroughput(new ProvisionedThroughput(2L, 2L)));
      TableUtils.waitUntilActive(dynamoDB, request.getTableName());
    };
  }
}
