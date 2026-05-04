package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;

class SportlerMeControllerTest extends BaseControllerTest {

    // POST /api/sportler/me

    @Test
    @WithMockUser(username = "new-user")
    void createMySportler_createsNew_returnsCreated() throws Exception {
        when(sportlerRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(sportlerRepository.findByEmailAndUserIdIsNull(anyString())).thenReturn(Optional.empty());
        when(mailValidatorService.isValid(any())).thenReturn(true);

        Sportler saved = new Sportler("new-user", "new@test.com", "Weiß", 0.0);
        ReflectionTestUtils.setField(saved, "id", "sp-new");
        saved.setUserId("new-user");
        when(sportlerRepository.save(any(Sportler.class))).thenReturn(saved);

        mockMvc.perform(post("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@test.com\",\"name\":\"new-user\",\"guertelgrad\":\"Weiß\",\"gewicht\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sp-new"))
                .andExpect(jsonPath("$.name").value("new-user"));
    }

    @Test
    @WithMockUser(username = "existing-user")
    void createMySportler_existingUser_returnsOk() throws Exception {
        Sportler existing = new Sportler("Existing", "existing@test.com", "Blau", 65.0);
        ReflectionTestUtils.setField(existing, "id", "sp-existing");
        existing.setUserId("existing-user");
        when(mailValidatorService.isValid(any())).thenReturn(true);
        when(sportlerRepository.findByUserId("existing-user")).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"existing@test.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sp-existing"));
    }

    @Test
    void createMySportler_requiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"x@test.com\"}"))
                .andExpect(status().isUnauthorized());
    }

    // PATCH /api/sportler/me

    @Test
    @WithMockUser(username = "test-user")
    void updateMySportler_returnsUpdatedSportler() throws Exception {
        Sportler sportler = new Sportler("Test", "test@test.com", "Braun", 70.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");
        sportler.setUserId("test-user");
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.of(sportler));
        when(sportlerRepository.save(any(Sportler.class))).thenReturn(sportler);

        mockMvc.perform(patch("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guertelgrad\":\"Schwarz\",\"gewicht\":72.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sp-1"));
    }

    @Test
    @WithMockUser(username = "test-user")
    void updateMySportler_sportlerNotFound_returns404() throws Exception {
        when(sportlerService.resolveCurrentSportler(anyString(), any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guertelgrad\":\"Schwarz\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMySportler_requiresAuthentication() throws Exception {
        mockMvc.perform(patch("/api/sportler/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guertelgrad\":\"Schwarz\"}"))
                .andExpect(status().isUnauthorized());
    }
}
