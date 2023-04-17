package com.github.simy4.poc.model;

import org.immutables.value.Value;

@Value.Style(
    jdkOnly = true,
    get = {"get*", "is*"})
public @interface Data {}
