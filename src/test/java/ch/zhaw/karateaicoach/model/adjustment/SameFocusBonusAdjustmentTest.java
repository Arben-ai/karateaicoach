package ch.zhaw.karateaicoach.model.adjustment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class SameFocusBonusAdjustmentTest {

    @ParameterizedTest
    @CsvSource({
        "0,0",
        "1,0",
        "2,10",
        "3,10",
        "4,20"
    })
    public void testSameFocusBonus(int count, double expected) {

        SameFocusBonusAdjustment adjustment = new SameFocusBonusAdjustment("Kata");

        List<Trainingsplan> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Trainingsplan tp = new Trainingsplan();
            tp.setFokus("Kata");
            list.add(tp);
        }

        double result = adjustment.calculateAdjustment(list);

        assertEquals(expected, result);
    }
}