package com.github.simy4.poc.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Data
@Value.Immutable
@JsonDeserialize(as = ImmutableCreateEntity.class)
public interface CreateEntity {
  String getName();

  default Entity toEntity() {
    return ImmutableEntity.builder().name(getName()).build();
  }
}
