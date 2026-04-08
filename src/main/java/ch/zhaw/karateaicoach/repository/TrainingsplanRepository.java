package ch.zhaw.karateaicoach.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusAggregationDTO;

public interface TrainingsplanRepository extends MongoRepository<Trainingsplan, String> {

    List<Trainingsplan> findByDauerGreaterThan(int dauer);

    List<Trainingsplan> findByStatus(TrainingsplanStatus status);

    List<Trainingsplan> findByDauerGreaterThanAndStatus(int dauer, TrainingsplanStatus status);

    @Aggregation({
    "{ '$match': { 'sportlerId': ?0 } }",
    "{ '$group': { '_id': '$status', 'count': { '$sum': 1 }, 'trainingsplanIds': { '$push': '$_id' } } }"
})
List<TrainingsplanStatusAggregationDTO> getTrainingsplanStatusAggregation(String sportlerId);
}