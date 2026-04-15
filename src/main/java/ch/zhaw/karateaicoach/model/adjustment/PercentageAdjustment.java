package ch.zhaw.karateaicoach.model.adjustment;

import java.util.List;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class PercentageAdjustment implements TrainingsplanAdjustment {

    private int percentage;

    public PercentageAdjustment(int percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateAdjustment(List<Trainingsplan> trainingsplaene) {

        double totalDuration = trainingsplaene.stream()
                .mapToDouble(Trainingsplan::getDauer)
                .sum();

        if (trainingsplaene.isEmpty()) {
            return 0.0;
        }

        return totalDuration * percentage / 100.0;
    }
}