package com.email.writer.app;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailGeneratorController.class)
class EmailGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailGeneratorService emailGeneratorService;

    @Test
    void testGenerateEmail() throws Exception {
        String requestJson = """
            {
                "emailContent": "Hey, I need help with my subscription.",
                "tone": "Casual"
            }
            """;

        when(emailGeneratorService.generateEmailReply(ArgumentMatchers.any()))
                .thenReturn("Sure! Let me help you with that.");

        mockMvc.perform(post("/api/email/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Sure! Let me help you with that."));
    }
}
