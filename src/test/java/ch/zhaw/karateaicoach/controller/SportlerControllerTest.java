package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;

class SportlerControllerTest extends BaseControllerTest {

    private Sportler sportlerAnna;
    private Sportler sportlerBen;

    @BeforeEach
    void setUp() {
        sportlerAnna = new Sportler("Anna", "anna@example.com", "Braun", 56.0);
        sportlerBen = new Sportler("Ben", "ben@example.com", "Schwarz", 72.5);
        ReflectionTestUtils.setField(sportlerAnna, "id", "sp-1");
        ReflectionTestUtils.setField(sportlerBen, "id", "sp-2");

        when(sportlerRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(sportlerAnna, sportlerBen),
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

    @Test
    void getAllSportlerRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/sportler"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "coach")
    void getSportlerByIdReturnsFound() throws Exception {
        when(sportlerRepository.findById("sp-1")).thenReturn(Optional.of(sportlerAnna));

        mockMvc.perform(get("/api/sportler/{id}", "sp-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anna"));
    }

    @Test
    @WithMockUser(username = "coach")
    void getSportlerByIdReturnsNotFound() throws Exception {
        when(sportlerRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sportler/{id}", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user")
    void getMySportlerReturnsFound() throws Exception {
        sportlerAnna.setUserId("test-user");
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.of(sportlerAnna));

        mockMvc.perform(get("/api/sportler/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anna"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getMySportlerReturnsNotFoundWhenNoMatch() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sportler/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user")
    void createSportlerReturnsCreated() throws Exception {
        Sportler saved = new Sportler("Hans", "hans@test.com", "Grün", 70.0);
        ReflectionTestUtils.setField(saved, "id", "sp-new");
        when(sportlerRepository.save(any(Sportler.class))).thenReturn(saved);
        when(mailValidatorService.isValid(any())).thenReturn(true);

        String requestBody = """
                {
                  "name": "Hans",
                  "email": "hans@test.com",
                  "guertelgrad": "Grün",
                  "gewicht": 70.0
                }
                """;

        mockMvc.perform(post("/api/sportler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sp-new"))
                .andExpect(jsonPath("$.name").value("Hans"));
    }

    @Test
    void createSportlerRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/sportler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"email\":\"x@x.com\",\"guertelgrad\":\"Weiß\",\"gewicht\":60}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "owner-user")
    void deleteSportlerAsOwnerSucceeds() throws Exception {
        sportlerAnna.setUserId("owner-user");
        when(sportlerRepository.findById("sp-1")).thenReturn(Optional.of(sportlerAnna));
        AtomicBoolean deleted = new AtomicBoolean(false);
        doAnswer(inv -> { deleted.set(true); return null; })
                .when(sportlerRepository).deleteById("sp-1");

        mockMvc.perform(delete("/api/sportler/{id}", "sp-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteSportlerAsAdminSucceeds() throws Exception {
        sportlerAnna.setUserId("other-user");
        when(sportlerRepository.findById("sp-1")).thenReturn(Optional.of(sportlerAnna));

        mockMvc.perform(delete("/api/sportler/{id}", "sp-1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other-user")
    void deleteSportlerAsNonOwnerReturnsForbidden() throws Exception {
        sportlerAnna.setUserId("owner-user");
        when(sportlerRepository.findById("sp-1")).thenReturn(Optional.of(sportlerAnna));

        mockMvc.perform(delete("/api/sportler/{id}", "sp-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "coach")
    void deleteSportlerNotFoundReturns404() throws Exception {
        when(sportlerRepository.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/sportler/{id}", "nonexistent"))
                .andExpect(status().isNotFound());
    }
}
