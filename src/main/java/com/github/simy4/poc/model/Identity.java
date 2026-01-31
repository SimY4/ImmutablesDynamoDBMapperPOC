package com.github.simy4.poc.model;

import org.jspecify.annotations.Nullable;

public record Identity(String getPk, @Nullable String getSk) {}
