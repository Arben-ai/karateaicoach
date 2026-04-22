package ch.zhaw.karateaicoach.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.zhaw.karateaicoach.security.TestSecurityConfig;

@SpringJUnitWebConfig(CreateRandomTrainingsplanControllerTest.TestConfig.class)
class CreateRandomTrainingsplanControllerTest {

    private static final String AI_RESPONSE = "Trainingsplan wurde erfolgreich erstellt.";

    @Configuration
    @EnableWebMvc
    @Import({ CreateRandomTrainingsplanController.class, TestSecurityConfig.class })
    static class TestConfig {

        @Bean
        ChatClient chatClient() {
            ChatClient.CallResponseSpec callSpec = mock(ChatClient.CallResponseSpec.class);
            ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
            ChatClient chatClient = mock(ChatClient.class);

            when(chatClient.prompt()).thenReturn(requestSpec);
            when(requestSpec.user(org.mockito.ArgumentMatchers.anyString())).thenReturn(requestSpec);
            when(requestSpec.call()).thenReturn(callSpec);
            when(callSpec.content()).thenReturn(AI_RESPONSE);

            return chatClient;
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
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
                .andExpect(status().isForbidden());
    }
}
