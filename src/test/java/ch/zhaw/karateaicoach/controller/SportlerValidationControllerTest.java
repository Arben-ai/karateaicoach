package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import ch.zhaw.karateaicoach.model.Sportler;

class SportlerValidationControllerTest extends BaseControllerTest {

    private Sportler sportlerAnna;

    @BeforeEach
    void setUp() {
        sportlerAnna = new Sportler("Anna", "anna@example.com", "Braun", 56.0);
        ReflectionTestUtils.setField(sportlerAnna, "id", "sp-1");
        sportlerAnna.setUserId("test-user");
    }

    @Test
    @WithMockUser(username = "test-user")
    void createSportlerWithInvalidEmailReturnsBadRequest() throws Exception {
        when(mailValidatorService.isValid(any())).thenReturn(false);

        String body = """
                {"name":"Test","email":"invalid-email","guertelgrad":"Grün","gewicht":70}
                """;

        mockMvc.perform(post("/api/sportler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test-user")
    void createSportlerWithDuplicateEmailReturnsConflict() throws Exception {
        when(mailValidatorService.isValid(any())).thenReturn(true);
        when(sportlerRepository.existsByEmail(anyString())).thenReturn(true);

        String body = """
                {"name":"Test","email":"anna@example.com","guertelgrad":"Grün","gewicht":70}
                """;

        mockMvc.perform(post("/api/sportler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "test-user")
    void createSportlerWithDuplicateNameReturnsConflict() throws Exception {
        when(mailValidatorService.isValid(any())).thenReturn(true);
        when(sportlerRepository.existsByEmail(anyString())).thenReturn(false);
        when(sportlerRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        String body = """
                {"name":"Anna","email":"new@example.com","guertelgrad":"Grün","gewicht":70}
                """;

        mockMvc.perform(post("/api/sportler")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "test-user")
    void getAllSportlerWithSearchFilter() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportlerAnna), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/sportler?search=Anna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Anna"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getAllSportlerWithGuertelgradFilter() throws Exception {
        when(sportlerRepository.findByGuertelgradIgnoreCase(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportlerAnna), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/sportler?guertelgrad=Braun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guertelgrad").value("Braun"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void getAllSportlerWithSearchAndGuertelgradFilter() throws Exception {
        when(sportlerRepository.findByNameContainingIgnoreCaseAndGuertelgradIgnoreCase(
                anyString(), anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(sportlerAnna), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/sportler?search=Anna&guertelgrad=Braun"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Anna"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void patchMySportlerUpdatesFields() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.of(sportlerAnna));
        when(sportlerRepository.save(any(Sportler.class))).thenReturn(sportlerAnna);

        mockMvc.perform(patch("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guertelgrad\":\"Schwarz\",\"gewicht\":60.5}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test-user")
    void patchMySportlerNotFoundReturns404() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any()))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guertelgrad\":\"Schwarz\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test-user")
    void getAllSportlerWithInvalidSizeReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/sportler?page=0&size=0"))
                .andExpect(status().isBadRequest());
    }
}
