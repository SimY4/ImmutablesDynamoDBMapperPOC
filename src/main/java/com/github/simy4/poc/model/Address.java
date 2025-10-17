package com.github.simy4.poc.model;

import org.immutables.value.Value;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@NullMarked
@Value.Builder
public record Address(String line1, @Nullable String line2, @Nullable String city, String country) {
  public static TableSchema<Address> schema() {
    return TableSchema.builder(Address.class, AddressBuilder.class)
        .newItemBuilder(AddressBuilder::new, AddressBuilder::build)
        .addAttribute(
            String.class, a -> a.name("line1").getter(Address::line1).setter(AddressBuilder::line1))
        .<@Nullable String>addAttribute(
            String.class, a -> a.name("line2").getter(Address::line2).setter(AddressBuilder::line2))
        .<@Nullable String>addAttribute(
            String.class, a -> a.name("city").getter(Address::city).setter(AddressBuilder::city))
        .addAttribute(
            String.class,
            a -> a.name("country").getter(Address::country).setter(AddressBuilder::country))
        .build();
  }
}
