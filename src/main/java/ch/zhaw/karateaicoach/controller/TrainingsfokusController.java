package ch.zhaw.karateaicoach.controller;

import ch.zhaw.karateaicoach.model.PaginatedResponseDTO;
import ch.zhaw.karateaicoach.model.Trainingsfokus;
import ch.zhaw.karateaicoach.model.TrainingsfokusCreateDTO;
import ch.zhaw.karateaicoach.model.TrainingsfokusStatus;
import ch.zhaw.karateaicoach.repository.TrainingsfokusRepository;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TrainingsfokusController {

    @Autowired
    TrainingsfokusRepository trainingsfokusRepository;

    @Autowired
    SportlerService sportlerService;

    @Autowired
    UserService userService;

    @PostMapping("/trainingsfokus")
    public ResponseEntity<Trainingsfokus> createTrainingsfokus(@RequestBody TrainingsfokusCreateDTO dto) {
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (!sportlerService.sportlerExists(dto.getSportlerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            Trainingsfokus fokus = new Trainingsfokus(
                    dto.getBeschreibung(),
                    dto.getSchwerpunkt(),
                    dto.getStatus(),
                    dto.getSportlerId());

            Trainingsfokus saved = trainingsfokusRepository.save(fokus);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsfokus/me")
    public ResponseEntity<?> getMyTrainingsfokus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String userId = userService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = userService.getCurrentUserEmail();
        String name = userService.getCurrentUserName();
        return sportlerService.resolveCurrentSportler(userId, email, name)
                .map(sportler -> {
                    Pageable pageable = PageRequest.of(page, size, Sort.by("schwerpunkt").ascending());
                    return ResponseEntity.<Object>ok(
                            PaginatedResponseDTO.fromPage(
                                    trainingsfokusRepository.findBySportlerId(sportler.getId(), pageable)));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Object>body(null));
    }

    @GetMapping("/trainingsfokus")
    public ResponseEntity<?> getAllTrainingsfokus(
            @RequestParam(required = false) String sportlerId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (page < 0 || size < 1) {
            return ResponseEntity.badRequest().body("Invalid pagination parameters");
        }

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("schwerpunkt").ascending());

            if (sportlerId != null) {
                return ResponseEntity.ok(
                        PaginatedResponseDTO.fromPage(
                                trainingsfokusRepository.findBySportlerId(sportlerId, pageable)));
            }

            if (status != null) {
                TrainingsfokusStatus enumStatus = TrainingsfokusStatus.valueOf(status);
                return ResponseEntity.ok(
                        PaginatedResponseDTO.fromPage(
                                trainingsfokusRepository.findByStatus(enumStatus, pageable)));
            }

            return ResponseEntity.ok(
                    PaginatedResponseDTO.fromPage(trainingsfokusRepository.findAll(pageable)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid parameter");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsfokus/{id}")
    public ResponseEntity<Trainingsfokus> getTrainingsfokusById(@PathVariable String id) {
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return trainingsfokusRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/trainingsfokus/{id}/status")
    public ResponseEntity<Trainingsfokus> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {

        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return trainingsfokusRepository.findById(id).map(fokus -> {
            try {
                fokus.setStatus(TrainingsfokusStatus.valueOf(status));
                return ResponseEntity.ok(trainingsfokusRepository.save(fokus));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).<Trainingsfokus>body(null);
            }
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/trainingsfokus/{id}")
    public ResponseEntity<Void> deleteTrainingsfokus(@PathVariable String id) {
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!trainingsfokusRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        trainingsfokusRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
