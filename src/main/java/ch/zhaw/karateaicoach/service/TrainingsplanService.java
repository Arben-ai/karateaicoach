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

        Optional<Trainingsplan> optionalPlan = trainingsplanRepository.findById(trainingsplanId);

        if (optionalPlan.isEmpty()) {
            return Optional.empty();
        }

        Trainingsplan plan = optionalPlan.get();

        if (plan.getStatus() != TrainingsplanStatus.DRAFT) {
            return Optional.empty();
        }

        plan.setStatus(TrainingsplanStatus.ACTIVE);
        trainingsplanRepository.save(plan);

        return Optional.of(plan);
    }

    public Optional<Trainingsplan> completeTrainingsplan(String trainingsplanId) {

        Optional<Trainingsplan> optionalPlan = trainingsplanRepository.findById(trainingsplanId);

        if (optionalPlan.isEmpty()) {
            return Optional.empty();
        }

        Trainingsplan plan = optionalPlan.get();

        // 🔴 WICHTIG: nur ACTIVE erlaubt
        if (plan.getStatus() != TrainingsplanStatus.ACTIVE) {
            return Optional.empty();
        }

        plan.setStatus(TrainingsplanStatus.COMPLETED);
        trainingsplanRepository.save(plan);

        return Optional.of(plan);
    }

    public Optional<Trainingsplan> archiveTrainingsplan(String trainingsplanId) {
        Optional<Trainingsplan> optionalPlan = trainingsplanRepository.findById(trainingsplanId);

        if (optionalPlan.isEmpty()) return Optional.empty();

        Trainingsplan plan = optionalPlan.get();

        if (plan.getStatus() != TrainingsplanStatus.COMPLETED) return Optional.empty();

        plan.setStatus(TrainingsplanStatus.ARCHIVED);
        trainingsplanRepository.save(plan);

        return Optional.of(plan);
    }
}