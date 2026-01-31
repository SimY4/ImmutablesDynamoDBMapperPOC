package com.github.simy4.poc.model;

import org.immutables.value.Value;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;

@Data
@NullMarked
@Value.Builder
@DynamoDbImmutable(builder = AddressBuilder.class)
public record Address(
    String line1, @Nullable String line2, @Nullable String city, String country) {}
