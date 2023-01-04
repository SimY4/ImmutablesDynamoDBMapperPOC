package com.github.simy4.poc.repositories;

import java.util.Optional;

public interface CrudRepository<A, Id> {
  A save(A a);

  Optional<A> get(Id id);

  void delete(Id id);
}
