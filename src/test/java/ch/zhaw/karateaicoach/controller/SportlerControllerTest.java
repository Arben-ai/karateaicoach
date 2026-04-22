package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;

class SportlerControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
        Sportler first = new Sportler("Anna", "anna@example.com", "Braun", 56.0);
        Sportler second = new Sportler("Ben", "ben@example.com", "Schwarz", 72.5);
        ReflectionTestUtils.setField(first, "id", "sp-1");
        ReflectionTestUtils.setField(second, "id", "sp-2");

        when(sportlerRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(first, second),
                        PageRequest.of(0, 2),
                        5));
    }

    @Test
    @WithMockUser(username = "coach")
    void getAllSportlerReturnsPagedResponse() throws Exception {
        mockMvc.perform(get("/api/sportler?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("sp-1"))
                .andExpect(jsonPath("$.content[0].name").value("Anna"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    @WithMockUser(username = "coach")
    void getAllSportlerReturnsBadRequestForInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/sportler?page=-1&size=5"))
                .andExpect(status().isBadRequest());
    }
}
