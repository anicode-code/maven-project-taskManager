package com.example.taskapi.integration.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "security.jwt.secret=Zm9yLXRlc3QtYmFzZTY0LXNlY3JldC1iYWJ5dA==",
    "security.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
public class AuthIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void register_login_me_delete_flow() throws Exception {
        var body = Map.of("username","intuser","password","pw");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("intuser"));

        String loginResp = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists()).andReturn().getResponse().getContentAsString();
        JsonNode node = om.readTree(loginResp);
        String token = node.get("token").asText();

        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("intuser"));

        mvc.perform(delete("/api/auth/delete").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
