package com.example.taskapi.integration.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
public class TaskIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    String token;

    @BeforeEach
    void setup() throws Exception {
        var reg = Map.of("username","taskint","password","pw");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isCreated());
        String loginResp = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode n = om.readTree(loginResp);
        token = n.get("token").asText();
    }

    @Test
    void create_list_get_update_delete() throws Exception {
        var t = Map.of("title","T1","description","d1","completed", false);
        String create = mvc.perform(post("/api/tasks").header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(t)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andReturn().getResponse().getContentAsString();
        long id = om.readTree(create).get("id").asLong();

        mvc.perform(get("/api/tasks").header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("T1"));
        mvc.perform(get("/api/tasks/"+id).header("Authorization","Bearer "+token)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value((int)id));

        var up = Map.of("title","T1-NEW","description","d1","completed", true);
        mvc.perform(put("/api/tasks/"+id).header("Authorization","Bearer "+token).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(up))).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("T1-NEW"));

        mvc.perform(delete("/api/tasks/"+id).header("Authorization","Bearer "+token)).andExpect(status().isNoContent());
        mvc.perform(get("/api/tasks/"+id).header("Authorization","Bearer "+token)).andExpect(status().is4xxClientError());
    }
}
