package ch.zhaw.karateaicoach.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import java.util.List;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;

public interface TrainingsplanRepository extends MongoRepository<Trainingsplan, String> {

    List<Trainingsplan> findByDauerGreaterThan(int dauer);

    List<Trainingsplan> findByStatus(TrainingsplanStatus status);

    List<Trainingsplan> findByDauerGreaterThanAndStatus(int dauer, TrainingsplanStatus status);

}