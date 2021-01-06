package com.github.simy4.poc.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.simy4.poc.model.converters.DynamoDBTypeConvertedInstant;
import com.github.simy4.poc.model.converters.DynamoDBTypeConverterIso;
import com.github.simy4.poc.model.generators.DynamoDBAutoGeneratedInstant;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

@Data
@Value.Immutable
@Value.Modifiable
@DynamoDBTable(tableName = "${db.entities.table-name}")
@DynamoDBTypeConverted(converter = Entity.Converter.class)
@JsonDeserialize(as = ImmutableEntity.class)
public interface Entity {
  String PK_PREFIX = "Entity#";

  static Identity id(String sk) {
    return ImmutableIdentity.of(PK_PREFIX, sk);
  }

  @Value.Auxiliary
  @JsonIgnore
  @DynamoDBIgnore
  default Identity getId() {
    return ImmutableIdentity.of(getPk(), getSk());
  }

  String getName();

  Address getAddress();

  List<Email> getEmails();

  @Value.Default
  @DynamoDBHashKey
  default String getPk() {
    return PK_PREFIX;
  }

  @DynamoDBRangeKey
  @DynamoDBAutoGeneratedKey
  @Nullable
  String getSk();

  @Nullable
  @DynamoDBAutoGeneratedInstant
  @DynamoDBTypeConvertedInstant
  Instant getUpdated();

  @Value.Default
  @DynamoDBTypeConvertedInstant
  default Instant getCreated() {
    return Instant.now();
  }

  final class Converter extends DynamoDBTypeConverterIso<ModifiableEntity, ImmutableEntity> {
    public Converter() {
      super(entity -> new ModifiableEntity().from(entity), ModifiableEntity::toImmutable);
    }
  }
}
