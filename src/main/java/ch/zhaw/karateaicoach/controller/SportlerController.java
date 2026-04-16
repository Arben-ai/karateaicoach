package ch.zhaw.karateaicoach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.PaginatedResponseDTO;
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
                    dto.getGewicht());

            Sportler saved = sportlerRepository.save(sportler);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/sportler")
    public ResponseEntity<?> getAllSportler(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest().body("Invalid pagination parameters");
            }

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by("name").ascending().and(Sort.by("id").ascending()));

            return ResponseEntity.ok(
                    PaginatedResponseDTO.fromPage(sportlerRepository.findAll(pageable)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/sportler/{id}")
    public ResponseEntity<Sportler> getSportlerById(@PathVariable String id) {
        return sportlerRepository.findById(id)
                .map(sportler -> ResponseEntity.ok(sportler))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
