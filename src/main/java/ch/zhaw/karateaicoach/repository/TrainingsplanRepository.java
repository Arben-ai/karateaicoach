package ch.zhaw.karateaicoach.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusAggregationDTO;

public interface TrainingsplanRepository extends MongoRepository<Trainingsplan, String> {

    Page<Trainingsplan> findBySportlerId(String sportlerId, Pageable pageable);

    Page<Trainingsplan> findByDauerGreaterThan(int dauer, Pageable pageable);

    Page<Trainingsplan> findByStatus(TrainingsplanStatus status, Pageable pageable);

    Page<Trainingsplan> findByDauerGreaterThanAndStatus(int dauer, TrainingsplanStatus status, Pageable pageable);

    @Aggregation({
    "{ '$match': { 'sportlerId': ?0 } }",
    "{ '$group': { '_id': '$status', 'count': { '$sum': 1 }, 'trainingsplanIds': { '$push': '$_id' } } }"
})
List<TrainingsplanStatusAggregationDTO> getTrainingsplanStatusAggregation(String sportlerId);
}
