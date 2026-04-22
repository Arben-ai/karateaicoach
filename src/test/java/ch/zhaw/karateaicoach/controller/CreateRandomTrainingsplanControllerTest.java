package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.test.context.support.WithMockUser;

class CreateRandomTrainingsplanControllerTest extends BaseControllerTest {

    private static final String AI_RESPONSE = "Trainingsplan wurde erfolgreich erstellt.";

    @BeforeEach
    void setUp() {
        ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn(AI_RESPONSE);
    }

    @Test
    @WithMockUser
    void createRandomReturnsAiResponse() throws Exception {
        mockMvc.perform(post("/api/trainingsplan/random"))
                .andExpect(status().isOk())
                .andExpect(content().string(AI_RESPONSE));
    }

    @Test
    void createRandomRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/trainingsplan/random"))
                .andExpect(status().isUnauthorized());
    }
}
