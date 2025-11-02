package com.example.taskapi.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    "security.jwt.secret=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=",
    "security.jwt.expiration-ms=3600000"
})
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void protected_endpoints_require_auth() throws Exception {
        // accept any 4xx client error (401 or 403)
        mvc.perform(get("/api/tasks"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void login_then_access_protected() throws Exception {
        var reg = Map.of("username","secuser","password","pw");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isCreated());
        String tokenResp = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(reg))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = om.readTree(tokenResp).get("token").asText();

        mvc.perform(get("/api/tasks").header("Authorization","Bearer "+token)).andExpect(status().isOk());
    }
}
