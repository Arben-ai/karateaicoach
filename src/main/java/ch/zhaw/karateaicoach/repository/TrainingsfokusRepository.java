package ch.zhaw.karateaicoach.repository;

import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainingsfokusRepository extends MongoRepository<Trainingsfokus, String> {

    Page<Trainingsfokus> findBySportlerId(String sportlerId, Pageable pageable);

    Page<Trainingsfokus> findByStatus(TrainingsfokusStatus status, Pageable pageable);

    Page<Trainingsfokus> findByKategorie(String kategorie, Pageable pageable);

    Page<Trainingsfokus> findBySportlerIdIn(java.util.List<String> sportlerIds, Pageable pageable);

    Page<Trainingsfokus> findByKategorieAndSportlerIdIn(String kategorie, java.util.List<String> sportlerIds, Pageable pageable);
}
