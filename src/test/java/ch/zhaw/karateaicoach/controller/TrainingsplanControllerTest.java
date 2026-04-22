package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsplan;

@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingsplanControllerTest extends BaseControllerTest {

    private static final String TEST_TITEL = "KI-generierter Trainingsplan";

    private final Map<String, Trainingsplan> repositoryState = new ConcurrentHashMap<>();
    private final AtomicInteger idSequence = new AtomicInteger(1);
    private String trainingsplanId;

    @BeforeEach
    void setUp() {
        ChatResponse mockChatResponse = mock(ChatResponse.class);
        Generation mockGeneration = mock(Generation.class);
        AssistantMessage mockAssistantMessage = mock(AssistantMessage.class);
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse);
        when(mockChatResponse.getResult()).thenReturn(mockGeneration);
        when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
        when(mockAssistantMessage.getText()).thenReturn(TEST_TITEL);

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
                .andExpect(jsonPath("$.titel").value(TEST_TITEL))
                .andReturn()
                .getResponse()
                .getContentAsString();

        trainingsplanId = JsonPath.read(responseBody, "$.id");
    }

    @Test
    @Order(2)
    void createTrainingsplanRequiresAdminRole() throws Exception {
        String requestBody = """
                {
                  "titel": "User Plan",
                  "dauer": 20,
                  "status": "DRAFT",
                  "sportlerId": "sportler-1"
                }
                """;

        mockMvc.perform(post("/api/trainingsplan")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getTrainingsplan() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titel").value(TEST_TITEL))
                .andExpect(jsonPath("$.dauer").value(45));
    }

    @Test
    @Order(4)
    void getTrainingsplanRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/{id}", "tp-forbidden")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteTrainingsplan() throws Exception {
        mockMvc.perform(delete("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getDeletedTrainingsplan() throws Exception {
        mockMvc.perform(get("/api/trainingsplan/{id}", trainingsplanId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void deleteTrainingsplanRequiresAdminRole() throws Exception {
        mockMvc.perform(delete("/api/trainingsplan/{id}", "tp-forbidden")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
