package com.github.simy4.poc.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
  @JsonProperty("active")
  ACTIVE,
  @JsonProperty("inactive")
  INACTIVE
}
