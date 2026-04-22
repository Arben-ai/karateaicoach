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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class ChatControllerTest extends BaseControllerTest {

    private static final String AI_RESPONSE = "Das ist eine KI-Antwort.";

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
    void chat_returnsAiResponse() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Wie soll ich trainieren?"))
                .andExpect(status().isOk())
                .andExpect(content().string(AI_RESPONSE));
    }

    @Test
    void chat_requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Wie soll ich trainieren?"))
                .andExpect(status().isUnauthorized());
    }
}
