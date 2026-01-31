package com.github.simy4.poc.repositories;

import com.github.simy4.poc.model.Identity;
import com.github.simy4.poc.model.ImmutableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Repository
public class EntityCrudRepository implements CrudRepository<ImmutableEntity, Identity> {
  private final DynamoDbAsyncTable<ImmutableEntity> dynamoDBTable;

  @Autowired
  public EntityCrudRepository(DynamoDbAsyncTable<ImmutableEntity> entityTable) {
    this.dynamoDBTable = entityTable;
  }

  protected Key fromId(Identity id) {
    return Key.builder().partitionValue(id.getPk()).sortValue(id.getSk()).build();
  }

  @Override
  public final CompletableFuture<ImmutableEntity> save(ImmutableEntity entity) {
    return dynamoDBTable.updateItem(entity).thenApply(Function.identity());
  }

  @Override
  public final CompletableFuture<Optional<ImmutableEntity>> get(Identity id) {
    return dynamoDBTable.getItem(fromId(id)).thenApply(Optional::ofNullable);
  }

  @Override
  public final CompletableFuture<Void> delete(Identity id) {
    return dynamoDBTable.deleteItem(fromId(id)).thenRun(() -> {});
  }
}
