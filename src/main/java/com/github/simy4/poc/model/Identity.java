package com.github.simy4.poc.model;

import org.springframework.lang.Nullable;

public record Identity(String getPk, @Nullable String getSk) {}
