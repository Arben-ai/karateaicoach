package ch.zhaw.karateaicoach.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.repository.SportlerRepository;

@Service
public class SportlerService {

    @Autowired
    private SportlerRepository sportlerRepository;

    public boolean sportlerExists(String sportlerId) {
        return sportlerRepository.existsById(sportlerId);
    }

    public List<Sportler> getAllSportler() {
        return sportlerRepository.findAll();
    }

    public void createSportler(String name, String email) {
        Sportler sportler = new Sportler(name, email, "", 0);
        sportlerRepository.save(sportler);
    }

    public Optional<Sportler> resolveCurrentSportler(String userId, String email, String name) {
        Optional<Sportler> byUserId = sportlerRepository.findByUserId(userId);
        if (byUserId.isPresent()) {
            return byUserId;
        }

        if (email != null) {
            Optional<Sportler> byEmail = sportlerRepository.findByEmailAndUserIdIsNull(email);
            if (byEmail.isPresent()) {
                Sportler sportler = byEmail.get();
                sportler.setUserId(userId);
                sportlerRepository.save(sportler);
                return Optional.of(sportler);
            }

            Sportler newSportler = new Sportler(name != null ? name : email, email, "", 0);
            newSportler.setUserId(userId);
            return Optional.of(sportlerRepository.save(newSportler));
        }

        return Optional.empty();
    }
}