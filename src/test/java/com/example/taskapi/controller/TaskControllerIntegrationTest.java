package com.example.taskapi.controller;

import com.example.taskapi.model.Task;
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
class TaskControllerIntegrationTest {

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    void postGetPutDelete_flow() throws Exception {
        // create
        Task create = new Task(null, "CtrlTest", "desc", false, null);
        // Task create = new Task(null, "CtrlTest", "desc", false);
        String createJson = objectMapper.writeValueAsString(create);

        String createdJson = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("CtrlTest")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Task created = objectMapper.readValue(createdJson, Task.class);
        Long id = created.getId();

        // get by id
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("CtrlTest")));

        // update
        Task update = new Task(null, "CtrlTest", "desc2", true, null);
        // Task update = new Task(null, "CtrlTestUpdated", "desc2", true);
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("CtrlTestUpdated")))
                .andExpect(jsonPath("$.completed", is(true)));

        // delete
        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());

        // verify gone
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound());
    }
}
