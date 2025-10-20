package com.example.taskapi.controller;

import com.example.taskapi.model.User;
import com.example.taskapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository repo;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    void postGetPutDelete_userFlow() throws Exception {
        User toCreate = new User(null, "frank", "pw");
        String json = objectMapper.writeValueAsString(toCreate);

        String createdJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is("frank")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User created = objectMapper.readValue(createdJson, User.class);
        Long id = created.getId();

        // get by id
        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("frank")));

        // get by username
        mockMvc.perform(get("/api/users/by-username/{username}", "frank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())));

        // update
        User update = new User(null, "frank2", "pw2");
        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("frank2")));

        // delete
        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        // verify gone
        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_duplicateUsername_shouldReturnBadRequestOrError() throws Exception {
        // create first
        User u = new User(null, "greg", "x");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u)))
                .andExpect(status().isCreated());

        // create duplicate - service will throw IllegalArgumentException -> by default becomes 500 unless handled.
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u)))
                .andExpect(status().isInternalServerError());
    }
}
