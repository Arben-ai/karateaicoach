package ch.zhaw.karateaicoach.model.adjustment;

import java.util.List;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class FixedBonusAdjustment implements TrainingsplanAdjustment {

    @Override
    public double calculateAdjustment(List<Trainingsplan> trainingsplaene) {

        double totalDuration = trainingsplaene.stream()
                .mapToDouble(Trainingsplan::getDauer)
                .sum();

        if (totalDuration >= 60) {
            return 10.0;
        }

        return 0.0;
    }
}