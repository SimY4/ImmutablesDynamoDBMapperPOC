package com.github.simy4.poc.model;

import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record CreateEntity(
    String name, @Valid Address address, @Valid List<Email> emails, Status status) {
  public Entity toEntity(String tenant) {
    return ImmutableEntity.builder()
        .tenant(tenant)
        .name(name())
        .address(address())
        .status(status())
        .addAllEmails(emails())
        .build();
  }
}
