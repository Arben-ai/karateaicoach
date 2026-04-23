package ch.zhaw.karateaicoach.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.karateaicoach.model.Sportler;

public interface SportlerRepository extends MongoRepository<Sportler, String> {
    Optional<Sportler> findByUserId(String userId);

    Optional<Sportler> findByEmailAndUserIdIsNull(String email);

    boolean existsByEmail(String email);

    boolean existsByNameIgnoreCase(String name);
}
