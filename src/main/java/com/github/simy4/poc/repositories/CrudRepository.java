package com.github.simy4.poc.repositories;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrudRepository<A, Id> {
  CompletableFuture<A> save(A a);

  CompletableFuture<Optional<A>> get(Id id);

  CompletableFuture<Void> delete(Id id);
}
