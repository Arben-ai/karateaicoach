package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsfokusRepository;
import ch.zhaw.karateaicoach.security.TestSecurityConfig;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;

@SpringJUnitWebConfig(TrainingsfokusControllerTest.TestConfig.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingsfokusControllerTest {

    @Configuration
    @EnableWebMvc
    @Import({ TrainingsfokusController.class, TestSecurityConfig.class, UserService.class })
    static class TestConfig {

        @Bean
        TrainingsfokusRepository trainingsfokusRepository() {
            return mock(TrainingsfokusRepository.class);
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
    private TrainingsfokusRepository trainingsfokusRepository;

    @Autowired
    private SportlerService sportlerService;

    private MockMvc mockMvc;

    private final Map<String, Trainingsfokus> repositoryState = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    private String fokusId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        when(sportlerService.sportlerExists(anyString())).thenReturn(true);

        when(trainingsfokusRepository.save(any(Trainingsfokus.class))).thenAnswer(invocation -> {
            Trainingsfokus fokus = invocation.getArgument(0);
            if (fokus.getId() == null) {
                ReflectionTestUtils.setField(fokus, "id", "tf-" + idSequence.getAndIncrement());
            }
            repositoryState.put(fokus.getId(), fokus);
            return fokus;
        });

        when(trainingsfokusRepository.findById(anyString())).thenAnswer(invocation ->
                Optional.ofNullable(repositoryState.get(invocation.getArgument(0))));

        when(trainingsfokusRepository.existsById(anyString())).thenAnswer(invocation ->
                repositoryState.containsKey(invocation.getArgument(0)));

        doAnswer(invocation -> {
            repositoryState.remove(invocation.getArgument(0));
            return null;
        }).when(trainingsfokusRepository).deleteById(anyString());

        when(trainingsfokusRepository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(List.copyOf(repositoryState.values())));
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createTrainingsfokus() throws Exception {
        String requestBody = """
                {
                  "beschreibung": "Fokus auf Grundtechniken",
                  "schwerpunkt": "Kata",
                  "status": "AKTIV",
                  "sportlerId": "sportler-1"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/trainingsfokus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.schwerpunkt").value("Kata"))
                .andExpect(jsonPath("$.status").value("AKTIV"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        fokusId = JsonPath.read(responseBody, "$.id");
    }

    @Test
    @Order(2)
    void createTrainingsfokusRequiresAdminRole() throws Exception {
        String requestBody = """
                {
                  "beschreibung": "Test",
                  "schwerpunkt": "Kumite",
                  "status": "AKTIV",
                  "sportlerId": "sportler-1"
                }
                """;

        mockMvc.perform(post("/api/trainingsfokus")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsfokusById() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus/{id}", fokusId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schwerpunkt").value("Kata"))
                .andExpect(jsonPath("$.beschreibung").value("Fokus auf Grundtechniken"));
    }

    @Test
    @Order(4)
    void getTrainingsfokusByIdRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus/{id}", fokusId)
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsfokusByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus/{id}", "nonexistent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllTrainingsfokus() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(7)
    void getAllTrainingsfokusRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(8)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateTrainingsfokusStatus() throws Exception {
        mockMvc.perform(put("/api/trainingsfokus/{id}/status", fokusId)
                        .param("status", "INAKTIV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INAKTIV"));
    }

    @Test
    @Order(9)
    void updateTrainingsfokusStatusRequiresAdminRole() throws Exception {
        mockMvc.perform(put("/api/trainingsfokus/{id}/status", fokusId)
                        .with(user("user").roles("USER"))
                        .param("status", "INAKTIV"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTrainingsfokus() throws Exception {
        mockMvc.perform(delete("/api/trainingsfokus/{id}", fokusId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getDeletedTrainingsfokusReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/trainingsfokus/{id}", fokusId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    void deleteTrainingsfokusRequiresAdminRole() throws Exception {
        mockMvc.perform(delete("/api/trainingsfokus/{id}", "tf-forbidden")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteNonExistentTrainingsfokusReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/trainingsfokus/{id}", "nonexistent-id"))
                .andExpect(status().isNotFound());
    }
}
