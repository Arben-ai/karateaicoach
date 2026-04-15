package ch.zhaw.karateaicoach.model.adjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class PercentageAdjustmentTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 50})
    public void testPercentageValues(int percentage) {

        PercentageAdjustment adjustment = new PercentageAdjustment(percentage);

        Trainingsplan tp = new Trainingsplan();
        tp.setDauer(100);

        double result = adjustment.calculateAdjustment(List.of(tp));

        assertEquals(percentage, result);
    }

    @Test
    public void testEmptyList() {
        PercentageAdjustment adjustment = new PercentageAdjustment(20);

        double result = adjustment.calculateAdjustment(List.of());

        assertEquals(0.0, result);
    }

    @Test
    public void testTwoTrainingsplaene() {
        PercentageAdjustment adjustment = new PercentageAdjustment(50);

        Trainingsplan tp1 = new Trainingsplan();
        tp1.setDauer(40);

        Trainingsplan tp2 = new Trainingsplan();
        tp2.setDauer(60);

        double result = adjustment.calculateAdjustment(List.of(tp1, tp2));

        assertEquals(50.0, result);
    }

    // ===== NEU: Exception Tests =====

    @Test
    void testPercentageGreaterThan50() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new PercentageAdjustment(60);
        });

        assertEquals("Error: Percentage must be less or equal 50.", exception.getMessage());
    }

    @Test
    void testPercentageEqualsZero() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new PercentageAdjustment(0);
        });

        assertEquals("Error: Percentage must be greater zero.", exception.getMessage());
    }

    @Test
    void testPercentageLessThanZero() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new PercentageAdjustment(-10);
        });

        assertEquals("Error: Percentage must be greater zero.", exception.getMessage());
    }

    @Test
void testWithMockito() {

    // Mock Trainingsplan 1
    Trainingsplan tp1 = mock(Trainingsplan.class);
    when(tp1.getDauer()).thenReturn(42);

    // Mock Trainingsplan 2
    Trainingsplan tp2 = mock(Trainingsplan.class);
    when(tp2.getDauer()).thenReturn(77);

    PercentageAdjustment adjustment = new PercentageAdjustment(10);

    double result = adjustment.calculateAdjustment(List.of(tp1, tp2));

    // 42 + 77 = 119 → 10% = 11.9
    assertEquals(11.9, result);
}
}