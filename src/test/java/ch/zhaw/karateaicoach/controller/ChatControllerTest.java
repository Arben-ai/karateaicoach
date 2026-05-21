package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

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

    @Test
    @WithMockUser(username = "test-user")
    void chat_withTrainingsplanAndSchwerpunkt_savesTrainingsplan() throws Exception {
        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.of(sportler));

        Trainingsplan saved = new Trainingsplan("Kata", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        when(trainingsplanRepository.save(any(Trainingsplan.class))).thenReturn(saved);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Erstelle einen Trainingsplan\nSchwerpunkt: Kata"))
                .andExpect(status().isOk())
                .andExpect(content().string(AI_RESPONSE));

        verify(trainingsplanRepository).save(any(Trainingsplan.class));
    }

    @Test
    @WithMockUser(username = "test-user")
    void chat_withTrainingsplanAndEmptySchwerpunkt_savesWithDefaultKarate() throws Exception {
        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.of(sportler));

        Trainingsplan saved = new Trainingsplan("Karate", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        when(trainingsplanRepository.save(any(Trainingsplan.class))).thenReturn(saved);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Erstelle einen Trainingsplan\nSchwerpunkt:\n"))
                .andExpect(status().isOk())
                .andExpect(content().string(AI_RESPONSE));

        verify(trainingsplanRepository).save(any(Trainingsplan.class));
    }

    @Test
    @WithMockUser(username = "test-user")
    void chat_withTrainingsplanButSportlerNotFound_doesNotSave() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Erstelle einen Trainingsplan\nSchwerpunkt: Kumite"))
                .andExpect(status().isOk())
                .andExpect(content().string(AI_RESPONSE));
    }
}
