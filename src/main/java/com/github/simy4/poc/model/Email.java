package com.github.simy4.poc.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.github.simy4.poc.model.converters.DynamoDBTypeConverterIso;
import org.immutables.value.Value;
import tools.jackson.databind.annotation.JsonDeserialize;

@Data
@Value.Immutable
@Value.Modifiable
@DynamoDBDocument
@DynamoDBTypeConverted(converter = Email.Converter.class)
@JsonDeserialize(as = ImmutableEmail.class)
@SuppressWarnings("this-escape")
public interface Email {
  @jakarta.validation.constraints.Email @Value.Parameter
  @Value.Redacted
  String getEmail();

  @Value.Default
  default boolean isVerified() {
    return false;
  }

  @Value.Default
  default boolean isPrimary() {
    return false;
  }

  final class Converter extends DynamoDBTypeConverterIso<ModifiableEmail, ImmutableEmail> {
    public Converter() {
      super(email -> new ModifiableEmail().from(email), ModifiableEmail::toImmutable);
    }
  }
}
