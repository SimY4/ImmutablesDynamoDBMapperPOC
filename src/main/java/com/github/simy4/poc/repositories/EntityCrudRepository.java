package com.github.simy4.poc.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.SaveBehavior;
import com.github.simy4.poc.model.Entity;
import com.github.simy4.poc.model.Identity;
import com.github.simy4.poc.model.ModifiableEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EntityCrudRepository implements CrudRepository<Entity, Identity> {
  private final DynamoDBMapper dynamoDBMapper;

  @Autowired
  public EntityCrudRepository(DynamoDBMapper dynamoDBMapper) {
    this.dynamoDBMapper = dynamoDBMapper;
  }

  protected ModifiableEntity fromId(Identity id) {
    var keyObject = new ModifiableEntity();
    keyObject.setPk(id.getPk());
    keyObject.setSk(id.getSk());
    return keyObject;
  }

  @Override
  public final Entity save(Entity entity) {
    var modifiable = new ModifiableEntity().from(entity);
    dynamoDBMapper.save(modifiable);
    return modifiable.toImmutable();
  }

  @Override
  public final Optional<Entity> get(Identity id) {
    return Optional.ofNullable(dynamoDBMapper.load(fromId(id))).map(ModifiableEntity::toImmutable);
  }

  @Override
  public final void delete(Identity id) {
    dynamoDBMapper.delete(
        fromId(id), DynamoDBMapperConfig.builder().withSaveBehavior(SaveBehavior.CLOBBER).build());
  }
}
