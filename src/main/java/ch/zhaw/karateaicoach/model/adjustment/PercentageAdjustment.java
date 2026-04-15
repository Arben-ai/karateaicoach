package ch.zhaw.karateaicoach.model.adjustment;

import java.util.List;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public class PercentageAdjustment implements TrainingsplanAdjustment {

    private final double percentage;

    public PercentageAdjustment(double percentage) {
        if (percentage > 50) {
            throw new RuntimeException("Error: Percentage must be less or equal 50.");
        }
        if (percentage <= 0) {
            throw new RuntimeException("Error: Percentage must be greater zero.");
        }
        this.percentage = percentage;
    }

    @Override
    public double calculateAdjustment(List<Trainingsplan> trainingsplaene) {
        double totalDuration = 0.0;

        for (Trainingsplan trainingsplan : trainingsplaene) {
            totalDuration += trainingsplan.getDauer();
        }

        return totalDuration * (percentage / 100.0);
    }
}