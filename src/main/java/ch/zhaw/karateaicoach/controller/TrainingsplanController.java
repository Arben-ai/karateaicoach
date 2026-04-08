package ch.zhaw.karateaicoach.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanCreateDTO;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;

@RestController
@RequestMapping("/api")
public class TrainingsplanController {

    @Autowired
    TrainingsplanRepository trainingsplanRepository;

    @Autowired
    SportlerService sportlerService;

    @PostMapping("/trainingsplan")
    public ResponseEntity<Trainingsplan> createTrainingsplan(@RequestBody TrainingsplanCreateDTO dto) {

        // 🔴 VALIDIERUNG: sportlerId prüfen
        if (!sportlerService.sportlerExists(dto.getSportlerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

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
    public ResponseEntity<?> getAllTrainingsplan(
            @RequestParam(required = false) Integer minDauer,
            @RequestParam(required = false) String status) {

        try {
            // Case 1: keine Parameter
            if (minDauer == null && status == null) {
                return ResponseEntity.ok(trainingsplanRepository.findAll());
            }

            // Case 2: nur minDauer
            if (status == null) {
                return ResponseEntity.ok(
                        trainingsplanRepository.findByDauerGreaterThan(minDauer));
            }

            // String → Enum umwandeln
            TrainingsplanStatus enumStatus = TrainingsplanStatus.valueOf(status);

            // Case 3: nur status
            if (minDauer == null) {
                return ResponseEntity.ok(
                        trainingsplanRepository.findByStatus(enumStatus));
            }

            // Case 4: beide Parameter
            return ResponseEntity.ok(
                    trainingsplanRepository.findByDauerGreaterThanAndStatus(minDauer, enumStatus));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid parameter");
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