package com.github.simy4.poc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Data
@Value.Immutable
@JsonDeserialize(as = ImmutableAddress.class)
public interface Address {
  static TableSchema<Address> schema() {
    return TableSchema.builder(Address.class, ImmutableAddress.Builder.class)
        .newItemBuilder(ImmutableAddress::builder, ImmutableAddress.Builder::build)
        .addAttribute(
            String.class,
            a -> a.name("line1").getter(Address::getLine1).setter(ImmutableAddress.Builder::line1))
        .addAttribute(
            String.class,
            a -> a.name("line2").getter(Address::getLine2).setter(ImmutableAddress.Builder::line2))
        .addAttribute(
            String.class,
            a -> a.name("city").getter(Address::getCity).setter(ImmutableAddress.Builder::city))
        .addAttribute(
            String.class,
            a ->
                a.name("country")
                    .getter(Address::getCountry)
                    .setter(ImmutableAddress.Builder::country))
        .build();
  }

  String getLine1();

  @Nullable String getLine2();

  @Nullable String getCity();

  String getCountry();
}
