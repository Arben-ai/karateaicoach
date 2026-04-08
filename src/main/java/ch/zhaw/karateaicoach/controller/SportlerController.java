package ch.zhaw.karateaicoach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.SportlerCreateDTO;
import ch.zhaw.karateaicoach.repository.SportlerRepository;

@RestController
@RequestMapping("/api")
public class SportlerController {

    @Autowired
    SportlerRepository sportlerRepository;

    @PostMapping("/sportler")
    public ResponseEntity<Sportler> createSportler(@RequestBody SportlerCreateDTO dto) {
        try {
            Sportler sportler = new Sportler(
                dto.getName(),
                dto.getEmail(),
                dto.getGuertelgrad(),
                dto.getGewicht()
            );

            Sportler saved = sportlerRepository.save(sportler);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}