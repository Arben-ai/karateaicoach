package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;

class TrainingsplanServiceTest {

    private TrainingsplanRepository trainingsplanRepository;
    private TrainingsplanService trainingsplanService;

    @BeforeEach
    void setUp() {
        trainingsplanRepository = mock(TrainingsplanRepository.class);
        trainingsplanService = new TrainingsplanService();
        ReflectionTestUtils.setField(trainingsplanService, "trainingsplanRepository", trainingsplanRepository);
    }

    @Test
    void activateTrainingsplan_success() {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.DRAFT, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-1");
        when(trainingsplanRepository.findById("tp-1")).thenReturn(Optional.of(plan));
        when(trainingsplanRepository.save(plan)).thenReturn(plan);

        Optional<Trainingsplan> result = trainingsplanService.activateTrainingsplan("tp-1");

        assertTrue(result.isPresent());
        assertEquals(TrainingsplanStatus.ACTIVE, result.get().getStatus());
        verify(trainingsplanRepository).save(plan);
    }

    @Test
    void activateTrainingsplan_notFound_returnsEmpty() {
        when(trainingsplanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainingsplan> result = trainingsplanService.activateTrainingsplan("nonexistent");

        assertTrue(result.isEmpty());
        verify(trainingsplanRepository, never()).save(any());
    }

    @Test
    void activateTrainingsplan_wrongStatus_returnsEmpty() {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.ACTIVE, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-2");
        when(trainingsplanRepository.findById("tp-2")).thenReturn(Optional.of(plan));

        Optional<Trainingsplan> result = trainingsplanService.activateTrainingsplan("tp-2");

        assertTrue(result.isEmpty());
        verify(trainingsplanRepository, never()).save(any());
    }

    @Test
    void completeTrainingsplan_success() {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.ACTIVE, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-3");
        when(trainingsplanRepository.findById("tp-3")).thenReturn(Optional.of(plan));
        when(trainingsplanRepository.save(plan)).thenReturn(plan);

        Optional<Trainingsplan> result = trainingsplanService.completeTrainingsplan("tp-3");

        assertTrue(result.isPresent());
        assertEquals(TrainingsplanStatus.COMPLETED, result.get().getStatus());
        verify(trainingsplanRepository).save(plan);
    }

    @Test
    void completeTrainingsplan_notFound_returnsEmpty() {
        when(trainingsplanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Trainingsplan> result = trainingsplanService.completeTrainingsplan("nonexistent");

        assertTrue(result.isEmpty());
        verify(trainingsplanRepository, never()).save(any());
    }

    @Test
    void completeTrainingsplan_wrongStatus_returnsEmpty() {
        Trainingsplan plan = new Trainingsplan("Kata", 60, TrainingsplanStatus.DRAFT, "sportler-1");
        ReflectionTestUtils.setField(plan, "id", "tp-4");
        when(trainingsplanRepository.findById("tp-4")).thenReturn(Optional.of(plan));

        Optional<Trainingsplan> result = trainingsplanService.completeTrainingsplan("tp-4");

        assertTrue(result.isEmpty());
        verify(trainingsplanRepository, never()).save(any());
    }

    private static <T> T any() {
        return org.mockito.ArgumentMatchers.any();
    }
}
