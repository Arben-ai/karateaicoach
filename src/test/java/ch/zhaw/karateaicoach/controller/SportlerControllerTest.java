package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.security.TestSecurityConfig;

@SpringJUnitWebConfig(SportlerControllerTest.TestConfig.class)
class SportlerControllerTest {

    @Configuration
    @EnableWebMvc
    @Import({ SportlerController.class, TestSecurityConfig.class })
    static class TestConfig {

        @Bean
        SportlerRepository sportlerRepository() {
            return mock(SportlerRepository.class);
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SportlerRepository sportlerRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "coach")
    void getAllSportlerReturnsPagedResponse() throws Exception {
        Sportler first = new Sportler("Anna", "anna@example.com", "Braun", 56.0);
        Sportler second = new Sportler("Ben", "ben@example.com", "Schwarz", 72.5);

        ReflectionTestUtils.setField(first, "id", "sp-1");
        ReflectionTestUtils.setField(second, "id", "sp-2");

        when(sportlerRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(first, second),
                        PageRequest.of(0, 2),
                        5));

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
