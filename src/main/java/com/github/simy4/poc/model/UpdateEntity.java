package com.github.simy4.poc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import java.util.Optional;

@Data
@Value.Immutable
@JsonDeserialize(as = ImmutableUpdateEntity.class)
public interface UpdateEntity {
  @Nullable
  String getName();

  @Valid
  @Nullable
  Address getAddress();

  @Nullable
  Status getStatus();

  default Entity patch(Entity entity) {
    return ImmutableEntity.copyOf(entity)
        .withName(Optional.ofNullable(getName()).orElseGet(entity::getName))
        .withAddress(Optional.ofNullable(getAddress()).orElseGet(entity::getAddress))
        .withStatus(Optional.ofNullable(getStatus()).orElseGet(entity::getStatus));
  }
}
