package com.github.simy4.poc.model;

import org.immutables.value.Value;
import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;

@Data
@NullMarked
@Value.Builder
@DynamoDbImmutable(builder = EmailBuilder.class)
public record Email(
    @jakarta.validation.constraints.Email String email, boolean verified, boolean primary) {}
