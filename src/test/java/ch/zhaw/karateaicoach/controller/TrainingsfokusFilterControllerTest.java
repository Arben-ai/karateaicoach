package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;

class TrainingsfokusFilterControllerTest extends BaseControllerTest {

    private Trainingsfokus fokus;

    @BeforeEach
    void setUp() {
        fokus = new Trainingsfokus("Beschreibung", "Kata", "Kumite", "Notiz",
                TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithStatusFilter() throws Exception {
        when(trainingsfokusRepository.findByStatus(any(TrainingsfokusStatus.class), any()))
                .thenReturn(new PageImpl<>(List.of(fokus), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/trainingsfokus?status=AKTIV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithSportlerIdFilter() throws Exception {
        when(trainingsfokusRepository.findBySportlerId(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(fokus), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/trainingsfokus?sportlerId=sp-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithSportlerNameFilter() throws Exception {
        ch.zhaw.karateaicoach.model.Sportler sportler = new ch.zhaw.karateaicoach.model.Sportler(
                "Anna", "anna@example.com", "Braun", 56.0);
        org.springframework.test.util.ReflectionTestUtils.setField(sportler, "id", "sp-1");

        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportler), PageRequest.of(0, 5), 1));
        when(trainingsfokusRepository.findBySportlerIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(fokus), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/trainingsfokus?sportlerName=Anna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithKategorieFilter() throws Exception {
        when(trainingsfokusRepository.findByKategorie(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(fokus), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/trainingsfokus?kategorie=Kumite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createTrainingsfokusSportlerNotExistsReturnsBadRequest() throws Exception {
        when(sportlerService.sportlerExists(anyString())).thenReturn(false);

        String body = """
                {"beschreibung":"Test","schwerpunkt":"Kata","kategorie":"Kumite",
                 "status":"AKTIV","sportlerId":"nonexistent"}
                """;

        mockMvc.perform(post("/api/trainingsfokus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateTrainingsfokusStatusNotFoundReturns404() throws Exception {
        when(trainingsfokusRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/trainingsfokus/{id}/status", "nonexistent")
                        .param("status", "INAKTIV"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateTrainingsfokusStatusWithInvalidStatusReturnsBadRequest() throws Exception {
        when(trainingsfokusRepository.findById(anyString())).thenReturn(Optional.of(fokus));

        mockMvc.perform(put("/api/trainingsfokus/{id}/status", "tf-1")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNonExistentTrainingsfokusReturns404() throws Exception {
        when(trainingsfokusRepository.existsById(anyString())).thenReturn(false);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/trainingsfokus/{id}", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithSportlerNameAndKategorieFilter() throws Exception {
        ch.zhaw.karateaicoach.model.Sportler sportler = new ch.zhaw.karateaicoach.model.Sportler(
                "Anna", "anna@example.com", "Braun", 56.0);
        org.springframework.test.util.ReflectionTestUtils.setField(sportler, "id", "sp-1");

        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(sportler), org.springframework.data.domain.PageRequest.of(0, 200), 1));
        when(trainingsfokusRepository.findByKategorieAndSportlerIdIn(anyString(), any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(fokus), org.springframework.data.domain.PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/trainingsfokus?sportlerName=Anna&kategorie=Kumite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithSportlerNameNoMatchReturnsEmpty() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(), org.springframework.data.domain.PageRequest.of(0, 200), 0));

        mockMvc.perform(get("/api/trainingsfokus?sportlerName=Unbekannt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithInvalidStatusReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus?status=UNGUELTIG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusWithInvalidPaginationReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus?page=-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokusNoFiltersReturnsAll() throws Exception {
        when(trainingsfokusRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(
                        List.of(fokus), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/trainingsfokus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
