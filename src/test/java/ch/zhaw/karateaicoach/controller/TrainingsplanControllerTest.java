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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.security.TestSecurityConfig;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;

@SpringJUnitWebConfig(TrainingsplanControllerTest.TestConfig.class)
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingsplanControllerTest {

    @Configuration
    @EnableWebMvc
    @Import({ TrainingsplanController.class, TestSecurityConfig.class, UserService.class })
    static class TestConfig {

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
    private TrainingsplanRepository trainingsplanRepository;

    @Autowired
    private SportlerService sportlerService;

    private MockMvc mockMvc;

    private final Map<String, Trainingsplan> repositoryState = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);

    private String trainingsplanId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        when(sportlerService.sportlerExists(anyString())).thenReturn(true);

        when(trainingsplanRepository.save(any(Trainingsplan.class))).thenAnswer(invocation -> {
            Trainingsplan plan = invocation.getArgument(0);

            if (plan.getId() == null) {
                ReflectionTestUtils.setField(plan, "id", "tp-" + idSequence.getAndIncrement());
            }

            repositoryState.put(plan.getId(), plan);
            return plan;
        });

        when(trainingsplanRepository.findById(anyString())).thenAnswer(invocation ->
                Optional.ofNullable(repositoryState.get(invocation.getArgument(0))));

        when(trainingsplanRepository.existsById(anyString())).thenAnswer(invocation ->
                repositoryState.containsKey(invocation.getArgument(0)));

        doAnswer(invocation -> {
            repositoryState.remove(invocation.getArgument(0));
            return null;
        }).when(trainingsplanRepository).deleteById(anyString());
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createTrainingsplan() throws Exception {
        String requestBody = """
                {
                  "titel": "Kata Intensiv",
                  "dauer": 45,
                  "status": "DRAFT",
                  "sportlerId": "sportler-1"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/trainingsplan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        trainingsplanId = JsonPath.read(responseBody, "$.id");
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsplan() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titel").value("Kata Intensiv"))
                .andExpect(jsonPath("$.dauer").value(45));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTrainingsplan() throws Exception {
        mockMvc.perform(delete("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getDeletedTrainingsplan() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(5)
    void deleteTrainingsplanRequiresAdminRole() throws Exception {
        mockMvc.perform(delete("/api/trainingsplan/{id}", "tp-forbidden")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
