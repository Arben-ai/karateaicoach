package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;

class TrainingsfokusMeControllerTest extends BaseControllerTest {

    // GET /api/trainingsfokus/me

    @Test
    @WithMockUser(username = "test-user")
    void getMyTrainingsfokus_returnsPagedResponse() throws Exception {
        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", "Kumite", null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(sportler));
        when(trainingsfokusRepository.findBySportlerId(eq("sp-1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(fokus)));

        mockMvc.perform(get("/api/trainingsfokus/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("tf-1"))
                .andExpect(jsonPath("$.content[0].schwerpunkt").value("Kata"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getMyTrainingsfokus_sportlerNotFound_returns404() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/trainingsfokus/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyTrainingsfokus_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus/me"))
                .andExpect(status().isUnauthorized());
    }

    // PATCH /api/trainingsfokus/{id}/gelesen

    @Test
    @WithMockUser(username = "owner-user")
    void markAsGelesen_asOwner_returnsOkAndGelesenTrue() throws Exception {
        Sportler sportler = new Sportler("Owner", "owner@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");

        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", null, null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        when(trainingsfokusRepository.findById("tf-1")).thenReturn(Optional.of(fokus));
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(sportler));
        when(trainingsfokusRepository.save(any(Trainingsfokus.class))).thenAnswer(inv -> {
            Trainingsfokus f = inv.getArgument(0);
            f.setGelesen(true);
            return f;
        });

        mockMvc.perform(patch("/api/trainingsfokus/{id}/gelesen", "tf-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gelesen").value(true));
    }

    @Test
    @WithMockUser(username = "other-user")
    void markAsGelesen_notOwner_returnsForbidden() throws Exception {
        Sportler otherSportler = new Sportler("Other", "other@test.com", "Blau", 65.0);
        ReflectionTestUtils.setField(otherSportler, "id", "sp-other");

        Trainingsfokus fokus = new Trainingsfokus("Beschreibung", "Kata", null, null, TrainingsfokusStatus.AKTIV, "sp-1");
        ReflectionTestUtils.setField(fokus, "id", "tf-1");

        when(trainingsfokusRepository.findById("tf-1")).thenReturn(Optional.of(fokus));
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(otherSportler));

        mockMvc.perform(patch("/api/trainingsfokus/{id}/gelesen", "tf-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test-user")
    void markAsGelesen_fokusNotFound_returns404() throws Exception {
        when(trainingsfokusRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/trainingsfokus/{id}/gelesen", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsGelesen_requiresAuthentication() throws Exception {
        mockMvc.perform(patch("/api/trainingsfokus/{id}/gelesen", "tf-1"))
                .andExpect(status().isUnauthorized());
    }
}
