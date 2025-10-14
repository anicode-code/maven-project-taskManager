package com.example.taskapi.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskModelIntegrationTest {

    @Test
    void lombok_shouldGenerateGettersSettersAndConstructors() {
        // Using AllArgsConstructor (id,title,desc,completed)
        Task t = new Task(10L, "Test", "desc", false);

        // Verify getters
        assertThat(t.getId()).isEqualTo(10L);
        assertThat(t.getTitle()).isEqualTo("Test");
        assertThat(t.getDescription()).isEqualTo("desc");
        assertThat(t.isCompleted()).isFalse();

        // Modify using setters
        t.setTitle("Modified");
        t.setCompleted(true);

        assertThat(t.getTitle()).isEqualTo("Modified");
        assertThat(t.isCompleted()).isTrue();

        // equals/hashCode: create equal object
        Task t2 = new Task(10L, "Modified", "desc", true);
        assertThat(t).isEqualTo(t2);
        assertThat(t.hashCode()).isEqualTo(t2.hashCode());
    }
}
