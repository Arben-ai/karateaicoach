package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

class TrainingsplanMeControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
        ChatResponse mockChatResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockAssistantMessage = mock(AssistantMessage.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
        when(mockAssistantMessage.getText()).thenReturn("KI-generierter Trainingsplan");
    }

    // POST /api/trainingsplan/aus-fokus/{fokusId}

    @Test
    @WithMockUser(username = "test-user")
    void createPlanFromFokus_success_returnsCreated() throws Exception {
        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", "Kumite", null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        Trainingsplan savedPlan = new Trainingsplan("KI-Plan: Kata", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        ReflectionTestUtils.setField(savedPlan, "id", "tp-new");

        when(trainingsfokusRepository.findById("tf-1")).thenReturn(Optional.of(fokus));
        when(trainingsplanRepository.findByTrainingsfokusId("tf-1")).thenReturn(Optional.empty());
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(sportler));
        when(trainingsplanRepository.save(any(Trainingsplan.class))).thenReturn(savedPlan);

        mockMvc.perform(post("/api/trainingsplan/aus-fokus/{fokusId}", "tf-1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tp-new"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void createPlanFromFokus_existingPlan_returnsOk() throws Exception {
        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", null, null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        Trainingsplan existing = new Trainingsplan("KI-Plan: Kata", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        ReflectionTestUtils.setField(existing, "id", "tp-existing");

        when(trainingsfokusRepository.findById("tf-1")).thenReturn(Optional.of(fokus));
        when(trainingsplanRepository.findByTrainingsfokusId("tf-1")).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/trainingsplan/aus-fokus/{fokusId}", "tf-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("tp-existing"));
    }

    @Test
    @WithMockUser(username = "other-user")
    void createPlanFromFokus_notOwner_returnsForbidden() throws Exception {
        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", null, null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        Sportler otherSportler = new Sportler("Other", "other@test.com", "Blau", 65.0);
        ReflectionTestUtils.setField(otherSportler, "id", "sp-other");

        when(trainingsfokusRepository.findById("tf-1")).thenReturn(Optional.of(fokus));
        when(trainingsplanRepository.findByTrainingsfokusId("tf-1")).thenReturn(Optional.empty());
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(otherSportler));

        mockMvc.perform(post("/api/trainingsplan/aus-fokus/{fokusId}", "tf-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user")
    void createPlanFromFokus_fokusNotFound_returns404() throws Exception {
        when(trainingsfokusRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/trainingsplan/aus-fokus/{fokusId}", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPlanFromFokus_requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/trainingsplan/aus-fokus/{fokusId}", "tf-1"))
                .andExpect(status().isUnauthorized());
    }

    // GET /api/trainingsplan/me

    @Test
    @WithMockUser(username = "test-user")
    void getMyTrainingsplan_returnsPagedResponse() throws Exception {
        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        Trainingsplan plan = new Trainingsplan("Kata Plan", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        ReflectionTestUtils.setField(plan, "id", "tp-1");

        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(sportler));
        when(trainingsplanRepository.findBySportlerId(eq("sp-1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(plan)));

        mockMvc.perform(get("/api/trainingsplan/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("tp-1"))
                .andExpect(jsonPath("$.content[0].titel").value("Kata Plan"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getMyTrainingsplan_sportlerNotFound_returns404() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainingsplan/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyTrainingsplan_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/me"))
                .andExpect(status().isUnauthorized());
    }
}
