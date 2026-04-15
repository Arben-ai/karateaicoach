package ch.zhaw.karateaicoach.model.adjustment;

import java.util.List;
import java.util.stream.Collectors;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class SameFocusBonusAdjustment implements TrainingsplanAdjustment {

    private String fokus;

    public SameFocusBonusAdjustment(String fokus) {
        this.fokus = fokus;
    }

    @Override
    public double calculateAdjustment(List<Trainingsplan> trainingsplaene) {

        List<Trainingsplan> filtered = trainingsplaene.stream()
                .filter(tp -> fokus.equals(tp.getFokus()))
                .collect(Collectors.toList());

        int count = filtered.size();

        int pairs = count / 2;

        return pairs * 10.0;
    }
}