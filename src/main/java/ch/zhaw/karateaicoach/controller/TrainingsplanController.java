package ch.zhaw.karateaicoach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanCreateDTO;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;

@RestController
@RequestMapping("/api")
public class TrainingsplanController {

    @Autowired
    TrainingsplanRepository trainingsplanRepository;

    @PostMapping("/trainingsplan")
    public ResponseEntity<Trainingsplan> createTrainingsplan(@RequestBody TrainingsplanCreateDTO dto) {
        try {
            Trainingsplan plan = new Trainingsplan(
                    dto.getTitel(),
                    dto.getDauer(),
                    dto.getStatus(),
                    dto.getSportlerId());

            Trainingsplan saved = trainingsplanRepository.save(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsplan")
    public ResponseEntity<Iterable<Trainingsplan>> getAllTrainingsplan() {
        try {
            return ResponseEntity.ok(trainingsplanRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsplan/{id}")
    public ResponseEntity<Trainingsplan> getTrainingsplanById(@PathVariable String id) {
        return trainingsplanRepository.findById(id)
                .map(plan -> ResponseEntity.ok(plan))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

}