package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

class TrainingsplanFilterControllerTest extends BaseControllerTest {

    private Sportler sportlerAnna;
    private Trainingsplan plan;

    @BeforeEach
    void setUp() {
        sportlerAnna = new Sportler("Anna", "anna@example.com", "Braun", 56.0);
        ReflectionTestUtils.setField(sportlerAnna, "id", "sp-1");

        plan = new Trainingsplan("Kata Plan", 60, TrainingsplanStatus.ACTIVE, "sp-1");
        ReflectionTestUtils.setField(plan, "id", "tp-1");
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithStatusFilter() throws Exception {
        when(trainingsplanRepository.findByStatus(any(TrainingsplanStatus.class), any()))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 8), 1));

        mockMvc.perform(get("/api/trainingsplan?status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithSportlerNameFilter() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportlerAnna), PageRequest.of(0, 8), 1));
        when(trainingsplanRepository.findBySportlerIdIn(any(), any()))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 8), 1));

        mockMvc.perform(get("/api/trainingsplan?sportlerName=Anna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithSportlerNameNoMatchReturnsEmpty() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 8), 0));

        mockMvc.perform(get("/api/trainingsplan?sportlerName=Unbekannt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithMinDauerAndStatusFilter() throws Exception {
        when(trainingsplanRepository.findByDauerGreaterThanAndStatus(any(Integer.class), any(), any()))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 8), 1));

        mockMvc.perform(get("/api/trainingsplan?minDauer=30&status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithMinDauerOnlyFilter() throws Exception {
        when(trainingsplanRepository.findByDauerGreaterThan(any(Integer.class), any()))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 8), 1));

        mockMvc.perform(get("/api/trainingsplan?minDauer=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllTrainingsplanRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/trainingsplan"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTrainingsplanAsUserReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/trainingsplan")
                        .with(user("athlete").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithSportlerNameAndStatusFilter() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportlerAnna), PageRequest.of(0, 200), 1));
        when(trainingsplanRepository.findByStatusAndSportlerIdIn(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/trainingsplan?sportlerName=Anna&status=ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithInvalidStatusReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/trainingsplan?status=UNGUELTIG"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanWithInvalidPaginationReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/trainingsplan?page=-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanNoFiltersReturnsAll() throws Exception {
        when(trainingsplanRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(plan), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/trainingsplan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createTrainingsplanSportlerNotExistsReturnsBadRequest() throws Exception {
        when(sportlerService.sportlerExists(anyString())).thenReturn(false);

        String body = """
                {"titel":"Test","dauer":60,"status":"ACTIVE","sportlerId":"nonexistent"}
                """;

        mockMvc.perform(post("/api/trainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
