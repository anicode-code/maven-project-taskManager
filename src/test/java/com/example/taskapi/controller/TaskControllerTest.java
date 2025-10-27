package com.example.taskapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "security.jwt.secret=Zm9yLXRlc3QtZG9uLXJlYWwtc2VjcmV0LWJ5dGVzMTIzNDU2",
        "security.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    private String tokenAlice;
    private String tokenBob;

    @BeforeEach
    public void setup() throws Exception {
        // register alice
        var alice = new AuthRequest("alice_test", "alicepw");
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(alice)))
            .andExpect(status().isCreated());

        // register bob
        var bob = new AuthRequest("bob_test", "bobpw");
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bob)))
            .andExpect(status().isCreated());

        // login alice -> token
        var resA = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(alice)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        tokenAlice = extractToken(resA);

        // login bob -> token
        var resB = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(bob)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        tokenBob = extractToken(resB);
    }

    @Test
    public void authenticated_user_can_create_and_list_their_tasks() throws Exception {
        var taskReq = new TaskDto("T1", "desc1", false);

        // alice creates a task
        mvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + tokenAlice)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(taskReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.title").value("T1"));

        // bob should have none
        mvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + tokenBob))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        // alice lists -> sees 1
        mvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer " + tokenAlice))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title").value("T1"));
    }

    @Test
    public void user_cannot_see_or_modify_others_task() throws Exception {
        // alice creates a task
        var t = new TaskDto("AliceTask", "a", false);
        var createResp = mvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + tokenAlice)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(t)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        JsonNode node = om.readTree(createResp);
        Long taskId = node.get("id").asLong();
        assertThat(taskId).isNotNull();

        // bob attempts to GET alice's task -> should get 404 (resource not found for this user)
        mvc.perform(get("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + tokenBob))
            .andExpect(status().is4xxClientError());

        // bob attempts to PUT alice's task -> 4xx
        var updated = new TaskDto("Hacked", "x", true);
        mvc.perform(put("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + tokenBob)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updated)))
            .andExpect(status().is4xxClientError());

        // bob attempts to DELETE -> 4xx
        mvc.perform(delete("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + tokenBob))
            .andExpect(status().is4xxClientError());

        // alice can still GET it
        mvc.perform(get("/api/tasks/" + taskId)
                .header("Authorization", "Bearer " + tokenAlice))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("AliceTask"));
    }

    @Test
    public void unauthenticated_access_is_forbidden() throws Exception {
        mvc.perform(get("/api/tasks"))
            .andExpect(status().isUnauthorized());
    }

    // helper to extract token JSON {"token":"..."}
    private static String extractToken(String responseBody) throws Exception {
        ObjectMapper l = new ObjectMapper();
        JsonNode n = l.readTree(responseBody);
        return n.get("token").asText();
    }

    // small DTOs
    static class AuthRequest {
        public String username;
        public String password;
        public AuthRequest() {}
        public AuthRequest(String u, String p) { this.username = u; this.password = p; }
    }
    static class TaskDto {
        public String title;
        public String description;
        public boolean completed;
        public TaskDto() {}
        public TaskDto(String t, String d, boolean c) { this.title = t; this.description = d; this.completed = c; }
    }
}
