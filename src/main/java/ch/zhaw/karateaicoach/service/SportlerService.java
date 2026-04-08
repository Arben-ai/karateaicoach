package ch.zhaw.karateaicoach.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.karateaicoach.repository.SportlerRepository;

@Service
public class SportlerService {

    @Autowired
    private SportlerRepository sportlerRepository;

    public boolean sportlerExists(String sportlerId) {
        return sportlerRepository.existsById(sportlerId);
    }
}