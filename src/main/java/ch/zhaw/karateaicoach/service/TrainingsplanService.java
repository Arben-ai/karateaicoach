package ch.zhaw.karateaicoach.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;

@Service
public class TrainingsplanService {

    @Autowired
    private TrainingsplanRepository trainingsplanRepository;

    public Optional<Trainingsplan> activateTrainingsplan(String trainingsplanId) {

        // 1. Trainingsplan laden
        Optional<Trainingsplan> optionalPlan = trainingsplanRepository.findById(trainingsplanId);

        if (optionalPlan.isEmpty()) {
            return Optional.empty();
        }

        Trainingsplan plan = optionalPlan.get();

        // 2. Status prüfen
        if (plan.getStatus() != TrainingsplanStatus.DRAFT) {
            return Optional.empty();
        }

        // 3. Status ändern
        plan.setStatus(TrainingsplanStatus.ACTIVE);

        // 4. speichern
        trainingsplanRepository.save(plan);

        return Optional.of(plan);
    }
}