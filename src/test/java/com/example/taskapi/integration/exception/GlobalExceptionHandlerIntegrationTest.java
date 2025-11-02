package com.example.taskapi.integration.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.taskapi.repository.TaskRepository;
import com.example.taskapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "security.jwt.secret=Zm9yLXRlc3QtYmFzZTY0LXNlY3JldC1iYWJ5dA==",
    "security.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
public class GlobalExceptionHandlerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired UserRepository userRepository;
    @Autowired TaskRepository taskRepository;

    @Test
    void missingTask_returns404Json() throws Exception {
        var body = Map.of("username","xe","password","pw");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body))).andExpect(status().isCreated());
        String loginResp = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = om.readTree(loginResp).get("token").asText();

        mvc.perform(get("/api/tasks/9999").header("Authorization","Bearer "+token))
            .andExpect(status().is4xxClientError())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error").exists());
    }
}
