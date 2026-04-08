package ch.zhaw.karateaicoach.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.karateaicoach.model.Trainingsplan;

public interface TrainingsplanRepository extends MongoRepository<Trainingsplan, String> {
}