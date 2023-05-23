package com.github.simy4.poc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Data
@Value.Immutable
@JsonDeserialize(as = ImmutableEmail.class)
public interface Email {
  static TableSchema<Email> schema() {
    return TableSchema.builder(Email.class, ImmutableEmail.Builder.class)
        .newItemBuilder(ImmutableEmail::builder, ImmutableEmail.Builder::build)
        .addAttribute(
            String.class,
            a -> a.name("email").getter(Email::getEmail).setter(ImmutableEmail.Builder::email))
        .addAttribute(
            Boolean.class,
            a ->
                a.name("verified")
                    .getter(Email::isVerified)
                    .setter(ImmutableEmail.Builder::verified))
        .addAttribute(
            Boolean.class,
            a -> a.name("primary").getter(Email::isPrimary).setter(ImmutableEmail.Builder::primary))
        .build();
  }

  @jakarta.validation.constraints.Email
  @Value.Parameter
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
}
