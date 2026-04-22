package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

class TrainingsplanPaginationControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanReturnsPagedResponse() throws Exception {
        Trainingsplan first = new Trainingsplan("Kata Basics", 30, TrainingsplanStatus.DRAFT, "sp-1");
        Trainingsplan second = new Trainingsplan("Kumite Power", 45, TrainingsplanStatus.ACTIVE, "sp-2");
        ReflectionTestUtils.setField(first, "id", "tp-1");
        ReflectionTestUtils.setField(second, "id", "tp-2");

        when(trainingsplanRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(first, second),
                        PageRequest.of(0, 2),
                        4));

        mockMvc.perform(get("/api/trainingsplan?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("tp-1"))
                .andExpect(jsonPath("$.content[0].titel").value("Kata Basics"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getFilteredTrainingsplanReturnsPagedResponse() throws Exception {
        Trainingsplan filtered = new Trainingsplan("Competition Prep", 60, TrainingsplanStatus.DRAFT, "sp-9");
        ReflectionTestUtils.setField(filtered, "id", "tp-9");

        when(trainingsplanRepository.findByDauerGreaterThanAndStatus(
                eq(45),
                eq(TrainingsplanStatus.DRAFT),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(filtered),
                        PageRequest.of(1, 1),
                        3));

        mockMvc.perform(get("/api/trainingsplan?minDauer=45&status=DRAFT&page=1&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("tp-9"))
                .andExpect(jsonPath("$.content[0].dauer").value(60))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsplanReturnsBadRequestForInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/trainingsplan?page=-1&size=5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAllTrainingsplanRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/trainingsplan?page=0&size=2"))
                .andExpect(status().isForbidden());
    }
}
