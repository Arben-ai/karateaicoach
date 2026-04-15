package ch.zhaw.karateaicoach.model.adjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

public class FixedBonusAdjustmentTest {

    @Test
    public void testEmptyList() {
        FixedBonusAdjustment adjustment = new FixedBonusAdjustment();

        double result = adjustment.calculateAdjustment(List.of());

        assertEquals(0.0, result);
    }

    @Test
    public void testBelowThreshold() {
        FixedBonusAdjustment adjustment = new FixedBonusAdjustment();

        Trainingsplan tp = new Trainingsplan();
        tp.setDauer(30);

        double result = adjustment.calculateAdjustment(List.of(tp));

        assertEquals(0.0, result);
    }

    @Test
    public void testAboveThreshold() {
        FixedBonusAdjustment adjustment = new FixedBonusAdjustment();

        Trainingsplan tp1 = new Trainingsplan();
        tp1.setDauer(30);

        Trainingsplan tp2 = new Trainingsplan();
        tp2.setDauer(40);

        double result = adjustment.calculateAdjustment(List.of(tp1, tp2));

        assertEquals(10.0, result);
    }
}