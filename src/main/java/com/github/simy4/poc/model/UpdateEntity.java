package com.github.simy4.poc.model;

import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
public record UpdateEntity(
    @Nullable String name, @Valid @Nullable Address address, @Nullable Status status) {
  public ImmutableEntity patch(ImmutableEntity entity) {
    return ImmutableEntity.builder()
        .name(Optional.ofNullable(name).orElseGet(entity::getName))
        .address(Optional.ofNullable(address).orElseGet(entity::getAddress))
        .status(Optional.ofNullable(status).orElseGet(entity::getStatus))
        .tenant(entity.getTenant())
        .sk(entity.getSk())
        .emails(entity.getEmails())
        .version(entity.getVersion())
        .created(entity.getCreated())
        .build();
  }
}
