package ch.zhaw.karateaicoach.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

class TrainingsplanArchiveControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void archiveTrainingsplan_success() throws Exception {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.ARCHIVED, "sp-1");
        ReflectionTestUtils.setField(plan, "id", "tp-1");
        when(trainingsplanService.archiveTrainingsplan("tp-1")).thenReturn(Optional.of(plan));

        mockMvc.perform(put("/api/service/archiveTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"tp-1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void archiveTrainingsplan_notFound_returnsBadRequest() throws Exception {
        when(trainingsplanService.archiveTrainingsplan("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/service/archiveTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"nonexistent\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void archiveTrainingsplan_requiresAuthentication() throws Exception {
        mockMvc.perform(put("/api/service/archiveTrainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"tp-1\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void archiveTrainingsplan_asNonAdmin_returnsForbidden() throws Exception {
        mockMvc.perform(put("/api/service/archiveTrainingsplan")
                        .with(user("athlete").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainingsplanId\": \"tp-1\"}"))
                .andExpect(status().isForbidden());
    }
}
