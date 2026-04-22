package ch.zhaw.karateaicoach.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsfokusRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.TrainingsplanService;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = {
        "spring.ai.openai.api-key=test-dummy-key",
        "spring.ai.openai.speech.api-key=test-dummy-key"
    }
)
abstract class BaseControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @MockitoBean
    protected TrainingsplanRepository trainingsplanRepository;

    @MockitoBean
    protected SportlerRepository sportlerRepository;

    @MockitoBean
    protected TrainingsfokusRepository trainingsfokusRepository;

    @MockitoBean
    protected TrainingsplanService trainingsplanService;

    @MockitoBean
    protected SportlerService sportlerService;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @MockitoBean
    protected MongoTemplate mongoTemplate;

    @MockitoBean
    protected OpenAiChatModel chatModel;

    @MockitoBean
    protected ChatClient chatClient;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }
}
