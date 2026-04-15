package ch.zhaw.karateaicoach.model.adjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}