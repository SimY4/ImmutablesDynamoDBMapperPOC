package com.github.simy4.poc.controllers;

import com.github.simy4.poc.IntegrationTest;
import com.github.simy4.poc.model.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@IntegrationTest
class EntityControllerIT {

  private static final String TENANT_ID = UUID.randomUUID().toString();
  private static final String CHANGE_ENTITY_PAYLOAD =
      """
          {
            "name": "name",
            "address": {
              "line1": "123 example st",
              "country": "Australia"
            },
            "emails": [{
              "email": "123@example.com",
              "verified": true,
              "primary": true
            }],
            "status": "active"
          }""";

  @Autowired private WebTestClient client;

  @Test
  void testCreateEntity() {
    client
        .post()
        .uri("/v1/entities")
        .header("X-tenant-id", TENANT_ID)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(CHANGE_ENTITY_PAYLOAD)
        .exchange()
        .expectAll(
            e -> e.expectStatus().isCreated(),
            e -> e.expectHeader().valueEquals(HttpHeaders.ETAG, "\"1\""),
            e -> e.expectHeader().exists(HttpHeaders.LOCATION))
        .expectBody()
        .json(CHANGE_ENTITY_PAYLOAD, false)
        .returnResult();
  }

  @Nested
  class ReadUpdateDelete {
    private Entity entity;

    @BeforeEach
    void setUp() {
      entity =
          client
              .post()
              .uri("/v1/entities")
              .header("X-tenant-id", TENANT_ID)
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(CHANGE_ENTITY_PAYLOAD)
              .exchange()
              .expectBody(Entity.class)
              .returnResult()
              .getResponseBody();
    }

    @Test
    void testReadEntity() {
      client
          .get()
          .uri("/v1/entities/{id}", entity.getId().getSk())
          .header("X-tenant-id", TENANT_ID)
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectAll(
              e -> e.expectStatus().isOk(),
              e -> e.expectHeader().valueEquals(HttpHeaders.ETAG, "\"1\""))
          .expectBody()
          .json(CHANGE_ENTITY_PAYLOAD, false);
      client
          .get()
          .uri("/v1/entities/{id}", entity.getId().getSk())
          .header("X-tenant-id", "wrong-tenant")
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isNotFound()
          .expectBody()
          .isEmpty();
    }

    @Test
    void testUpdateEntity() {
      client
          .patch()
          .uri("/v1/entities/{id}", entity.getId().getSk())
          .header("X-tenant-id", TENANT_ID)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue("{\"name\":\"other-name\"}")
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody()
          .json("{\"name\":\"other-name\"}", false);
    }

    @Test
    void testDeleteEntity() {
      client
          .delete()
          .uri("/v1/entities/{id}", entity.getId().getSk())
          .header("X-tenant-id", TENANT_ID)
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isNoContent()
          .expectBody()
          .isEmpty();
    }
  }
}
