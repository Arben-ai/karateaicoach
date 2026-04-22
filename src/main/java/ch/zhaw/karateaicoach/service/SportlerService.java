package ch.zhaw.karateaicoach.service;

import java.util.List;

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
}