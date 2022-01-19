package com.github.simy4.poc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simy4.poc.IntegrationTest;
import com.github.simy4.poc.model.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

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

  @Autowired private WebApplicationContext webApplicationContext;
  @Autowired private ObjectMapper objectMapper;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void testCreateEntity() throws Exception {
    mockMvc
        .perform(
            post("/v1/entities")
                .header("X-tenant-id", TENANT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CHANGE_ENTITY_PAYLOAD))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(content().json(CHANGE_ENTITY_PAYLOAD, false));
  }

  @Nested
  class ReadUpdateDelete {
    private Entity entity;

    @BeforeEach
    void setUp() throws Exception {
      entity =
          objectMapper.readValue(
              mockMvc
                  .perform(
                      post("/v1/entities")
                          .header("X-tenant-id", TENANT_ID)
                          .accept(MediaType.APPLICATION_JSON)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(CHANGE_ENTITY_PAYLOAD))
                  .andExpect(status().isCreated())
                  .andReturn()
                  .getResponse()
                  .getContentAsString(StandardCharsets.UTF_8),
              Entity.class);
    }

    @Test
    void testReadEntity() throws Exception {
      mockMvc
          .perform(
              get("/v1/entities/{id}", entity.getId().getSk())
                  .header("X-tenant-id", TENANT_ID)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
          .andExpect(content().json(CHANGE_ENTITY_PAYLOAD, false));
      mockMvc
          .perform(
              get("/v1/entities/{id}", entity.getId().getSk())
                  .header("X-tenant-id", "wrong-tenant")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEntity() throws Exception {
      mockMvc
          .perform(
              patch("/v1/entities/{id}", entity.getId().getSk())
                  .header("X-tenant-id", TENANT_ID)
                  .accept(MediaType.APPLICATION_JSON)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\":\"other-name\"}"))
          .andExpect(status().isOk())
          .andExpect(content().json("{\"name\":\"other-name\"}", false));
    }

    @Test
    void testDeleteEntity() throws Exception {
      mockMvc
          .perform(
              delete("/v1/entities/{id}", entity.getId().getSk()).header("X-tenant-id", TENANT_ID))
          .andExpect(status().isNoContent());

      mockMvc
          .perform(
              get("/v1/entities/{id}", entity.getId().getSk())
                  .header("X-tenant-id", TENANT_ID)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}
