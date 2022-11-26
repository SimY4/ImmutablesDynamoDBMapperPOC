package com.github.simy4.poc.controllers;

import com.github.simy4.poc.model.CreateEntity;
import com.github.simy4.poc.model.Entity;
import com.github.simy4.poc.model.Identity;
import com.github.simy4.poc.model.UpdateEntity;
import com.github.simy4.poc.repositories.CrudRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Objects;

/** Sample CRUD REST controller. */
@RestController
@RequestMapping("/v1/entities")
public class EntityController {

  private final CrudRepository<Entity, Identity> crudRepository;

  @Autowired
  public EntityController(CrudRepository<Entity, Identity> crudRepository) {
    this.crudRepository = crudRepository;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      responses =
          @ApiResponse(
              responseCode = "201",
              headers = {@Header(name = HttpHeaders.ETAG), @Header(name = HttpHeaders.LOCATION)}))
  public ResponseEntity<Entity> createEntity(
      @RequestHeader("X-tenant-id") String tenant, @Valid @RequestBody CreateEntity entity) {
    var created = crudRepository.save(entity.toEntity(tenant));
    return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId().getSk())
                .toUri())
        .eTag(Objects.toString(created.getVersion(), "0"))
        .body(created);
  }

  @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      responses = {
        @ApiResponse(
            responseCode = "200",
            headers = {@Header(name = HttpHeaders.ETAG)}),
        @ApiResponse(responseCode = "404", content = @Content)
      })
  public ResponseEntity<Entity> getEntity(
      @RequestHeader("X-tenant-id") String tenant, @PathVariable("id") String id) {
    return crudRepository
        .get(Entity.id(tenant, id))
        .map(e -> ResponseEntity.ok().eTag(Objects.toString(e.getVersion(), "0")).body(e))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping(
      path = "{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      responses = {
        @ApiResponse(
            responseCode = "200",
            headers = {@Header(name = HttpHeaders.ETAG)}),
        @ApiResponse(responseCode = "404", content = @Content)
      })
  public ResponseEntity<Entity> updateEntity(
      @RequestHeader("X-tenant-id") String tenant,
      @PathVariable("id") String id,
      @Valid @RequestBody UpdateEntity entity) {
    return crudRepository
        .get(Entity.id(tenant, id))
        .map(e -> crudRepository.save(entity.patch(e)))
        .map(e -> ResponseEntity.ok().eTag(Objects.toString(e.getVersion(), "0")).body(e))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(path = "{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteEntity(
      @RequestHeader("X-tenant-id") String tenant, @PathVariable("id") String id) {
    crudRepository.get(Entity.id(tenant, id)).ifPresent(crudRepository::delete);
  }
}
