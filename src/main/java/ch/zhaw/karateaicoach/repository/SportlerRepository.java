package ch.zhaw.karateaicoach.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.karateaicoach.model.Sportler;

public interface SportlerRepository extends MongoRepository<Sportler, String> {
}