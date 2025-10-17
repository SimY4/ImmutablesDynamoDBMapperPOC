package com.github.simy4.poc.model;

import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public record UpdateEntity(
    @Nullable String name, @Valid @Nullable Address address, @Nullable Status status) {
  public Entity patch(Entity entity) {
    return ImmutableEntity.copyOf(entity)
        .withName(Optional.ofNullable(name()).orElseGet(entity::getName))
        .withAddress(Optional.ofNullable(address()).orElseGet(entity::getAddress))
        .withStatus(Optional.ofNullable(status()).orElseGet(entity::getStatus));
  }
}
