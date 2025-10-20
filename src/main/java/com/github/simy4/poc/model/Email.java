package com.github.simy4.poc.model;

import org.immutables.value.Value;
import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@NullMarked
@Value.Builder
public record Email(
    @jakarta.validation.constraints.Email String email, boolean verified, boolean primary) {
  static TableSchema<Email> schema() {
    return TableSchema.builder(Email.class, EmailBuilder.class)
        .newItemBuilder(EmailBuilder::new, EmailBuilder::build)
        .addAttribute(
            String.class, a -> a.name("email").getter(Email::email).setter(EmailBuilder::email))
        .addAttribute(
            Boolean.class,
            a -> a.name("verified").getter(Email::verified).setter(EmailBuilder::verified))
        .addAttribute(
            Boolean.class,
            a -> a.name("primary").getter(Email::primary).setter(EmailBuilder::primary))
        .build();
  }
}
