package ch.zhaw.karateaicoach.model.adjustment;

import java.util.List;

import ch.zhaw.karateaicoach.model.Trainingsplan;

public interface TrainingsplanAdjustment {

    double calculateAdjustment(List<Trainingsplan> trainingsplaene);

}