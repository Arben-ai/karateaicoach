package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.security.TestSecurityConfig;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.TrainingsplanService;

@SpringJUnitWebConfig(TrainingsplanServiceControllerTest.TestConfig.class)
class TrainingsplanServiceControllerTest {

    @Configuration
    @EnableWebMvc
    @Import({ TrainingsplanServiceController.class, TestSecurityConfig.class })
    static class TestConfig {

        @Bean
        TrainingsplanService trainingsplanService() {
            return mock(TrainingsplanService.class);
        }

        @Bean
        TrainingsplanRepository trainingsplanRepository() {
            return mock(TrainingsplanRepository.class);
        }

        @Bean
        SportlerService sportlerService() {
            return mock(SportlerService.class);
        }

        @Bean
        SportlerRepository sportlerRepository() {
            return mock(SportlerRepository.class);
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TrainingsplanService trainingsplanService;

    @Autowired
    private TrainingsplanRepository trainingsplanRepository;

    @Autowired
    private SportlerService sportlerService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

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
