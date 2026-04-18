package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.repository.SportlerRepository;

class SportlerServiceTest {

    private SportlerRepository sportlerRepository;
    private SportlerService sportlerService;

    @BeforeEach
    void setUp() {
        sportlerRepository = mock(SportlerRepository.class);
        sportlerService = new SportlerService();
        ReflectionTestUtils.setField(sportlerService, "sportlerRepository", sportlerRepository);
    }

    @Test
    void sportlerExists_returnsTrue_whenExists() {
        when(sportlerRepository.existsById("sportler-1")).thenReturn(true);
        assertTrue(sportlerService.sportlerExists("sportler-1"));
    }

    @Test
    void sportlerExists_returnsFalse_whenNotExists() {
        when(sportlerRepository.existsById("nonexistent")).thenReturn(false);
        assertFalse(sportlerService.sportlerExists("nonexistent"));
    }
}
