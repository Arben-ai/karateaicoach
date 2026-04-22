package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
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

    @Test
    void getAllSportler_returnsAllFromRepository() {
        Sportler s1 = new Sportler("Anna", "anna@test.com", "Schwarz", 60.0);
        Sportler s2 = new Sportler("Ben", "ben@test.com", "Braun", 75.0);
        when(sportlerRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Sportler> result = sportlerService.getAllSportler();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getName());
    }

    @Test
    void createSportler_savesNewSportlerWithNameAndEmail() {
        when(sportlerRepository.save(any(Sportler.class))).thenAnswer(inv -> inv.getArgument(0));

        sportlerService.createSportler("Max", "max@test.com");

        verify(sportlerRepository).save(any(Sportler.class));
    }
}
