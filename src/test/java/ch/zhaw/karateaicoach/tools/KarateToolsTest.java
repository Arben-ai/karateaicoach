package ch.zhaw.karateaicoach.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;

class KarateToolsTest {

    private SportlerService sportlerService;
    private TrainingsplanRepository trainingsplanRepository;
    private KarateTools karateTools;

    @BeforeEach
    void setUp() {
        sportlerService = mock(SportlerService.class);
        trainingsplanRepository = mock(TrainingsplanRepository.class);
        karateTools = new KarateTools();
        ReflectionTestUtils.setField(karateTools, "sportlerService", sportlerService);
        ReflectionTestUtils.setField(karateTools, "trainingsplanRepository", trainingsplanRepository);
    }

    @Test
    void getAllSportler_returnsListFromService() {
        Sportler sportler = new Sportler("Anna", "anna@test.com", "Schwarz", 60.0);
        when(sportlerService.getAllSportler()).thenReturn(List.of(sportler));

        List<Sportler> result = karateTools.getAllSportler();

        assertEquals(1, result.size());
        assertEquals("Anna", result.get(0).getName());
    }

    @Test
    void getAllTrainingsplaene_returnsListFromRepository() {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.DRAFT, "sp-1");
        when(trainingsplanRepository.findAll()).thenReturn(List.of(plan));

        List<Trainingsplan> result = karateTools.getAllTrainingsplaene();

        assertEquals(1, result.size());
        assertEquals("Kata", result.get(0).getTitel());
    }

    @Test
    void createSportler_delegatesToService() {
        karateTools.createSportler("Max", "max@test.com");

        verify(sportlerService).createSportler("Max", "max@test.com");
    }

    @Test
    void createTrainingsplan_withExistingSportler_savesplan() {
        Sportler sportler = new Sportler("Anna", "anna@test.com", "Schwarz", 60.0);
        ReflectionTestUtils.setField(sportler, "id", "sp-1");
        when(sportlerService.getAllSportler()).thenReturn(List.of(sportler));

        karateTools.createTrainingsplan("Kata Plan", 45, "anna");

        verify(trainingsplanRepository).save(any(Trainingsplan.class));
    }

    @Test
    void createTrainingsplan_withNewSportler_createsSportlerFirst() {
        Sportler created = new Sportler("Neu", "", "", 0);
        ReflectionTestUtils.setField(created, "id", "sp-new");
        when(sportlerService.getAllSportler())
                .thenReturn(List.of())
                .thenReturn(List.of(created));

        karateTools.createTrainingsplan("Plan", 30, "Neu");

        verify(sportlerService).createSportler("Neu", "");
        verify(trainingsplanRepository).save(any(Trainingsplan.class));
    }
}
