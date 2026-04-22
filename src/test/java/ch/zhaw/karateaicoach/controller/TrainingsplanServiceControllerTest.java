package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

class TrainingsplanServiceControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void activateTrainingsplan_success() throws Exception {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.ACTIVE, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-1");
        when(trainingsplanService.activateTrainingsplan("tp-1")).thenReturn(Optional.of(plan));

        mockMvc.perform(put("/api/service/activateTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"tp-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void activateTrainingsplan_notFound_returnsBadRequest() throws Exception {
        when(trainingsplanService.activateTrainingsplan("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/service/activateTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"nonexistent\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void completeTrainingsplan_success() throws Exception {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.COMPLETED, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-2");
        when(trainingsplanService.completeTrainingsplan("tp-2")).thenReturn(Optional.of(plan));

        mockMvc.perform(put("/api/service/completeTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"tp-2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void completeTrainingsplan_notFound_returnsBadRequest() throws Exception {
        when(trainingsplanService.completeTrainingsplan("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/service/completeTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"nonexistent\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsplanDashboard_success() throws Exception {
        when(sportlerService.sportlerExists("sportler-1")).thenReturn(true);
        when(trainingsplanRepository.getTrainingsplanStatusAggregation("sportler-1"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/service/trainingsplanDashboard")
                        .param("sportlerId", "sportler-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsplanDashboard_sportlerNotFound_returnsBadRequest() throws Exception {
        when(sportlerService.sportlerExists(anyString())).thenReturn(false);

        mockMvc.perform(get("/api/service/trainingsplanDashboard")
                        .param("sportlerId", "nonexistent"))
                .andExpect(status().isBadRequest());
    }
}
