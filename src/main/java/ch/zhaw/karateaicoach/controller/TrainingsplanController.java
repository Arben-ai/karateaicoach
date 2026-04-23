package ch.zhaw.karateaicoach.controller;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.PaginatedResponseDTO;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanCreateDTO;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;

@RestController
@RequestMapping("/api")
public class TrainingsplanController {

    @Autowired
    OpenAiChatModel chatModel;

    @Autowired
    TrainingsplanRepository trainingsplanRepository;

    @Autowired
    SportlerRepository sportlerRepository;

    @Autowired
    SportlerService sportlerService;

    @Autowired
    UserService userService;

    @PostMapping("/trainingsplan")
    public ResponseEntity<Trainingsplan> createTrainingsplan(@RequestBody TrainingsplanCreateDTO dto) {

        // 🔒 ADMIN CHECK
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        if (!sportlerService.sportlerExists(dto.getSportlerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            var generatedTitle = chatModel.call(new Prompt(
                "Der Titel lautet bisher: '" + dto.getTitel() + "'. Falls nötig, verbessere den Titel anhand des Trainingsfokus: " + dto.getFokus() + " und der Dauer: " + dto.getDauer() + " Minuten. Gib nur den neuen Titel zurück."
            ));
            var titel = generatedTitle.getResult().getOutput().getText();

            Trainingsplan plan = new Trainingsplan(
                    titel,
                    dto.getDauer(),
                    dto.getStatus(),
                    dto.getSportlerId());

            Trainingsplan saved = trainingsplanRepository.save(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsplan/me")
    public ResponseEntity<?> getMyTrainingsplan(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String userId = userService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        return sportlerRepository.findByUserId(userId)
                .map(sportler -> {
                    Pageable pageable = PageRequest.of(
                            page, size, Sort.by("erstelldatum").descending().and(Sort.by("id").ascending()));
                    return ResponseEntity.<Object>ok(
                            PaginatedResponseDTO.fromPage(
                                    trainingsplanRepository.findBySportlerId(sportler.getId(), pageable)));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Object>body(null));
    }

    @GetMapping("/trainingsplan")
    public ResponseEntity<?> getAllTrainingsplan(
            @RequestParam(required = false) Integer minDauer,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 🔒 ADMIN CHECK
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest().body("Invalid pagination parameters");
            }

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by("erstelldatum").descending().and(Sort.by("id").ascending()));

            if (minDauer == null && status == null) {
                return ResponseEntity.ok(
                        PaginatedResponseDTO.fromPage(trainingsplanRepository.findAll(pageable)));
            }

            if (status == null) {
                return ResponseEntity.ok(
                        PaginatedResponseDTO.fromPage(
                                trainingsplanRepository.findByDauerGreaterThan(minDauer, pageable)));
            }

            TrainingsplanStatus enumStatus = TrainingsplanStatus.valueOf(status);

            if (minDauer == null) {
                return ResponseEntity.ok(
                        PaginatedResponseDTO.fromPage(
                                trainingsplanRepository.findByStatus(enumStatus, pageable)));
            }

            return ResponseEntity.ok(
                    PaginatedResponseDTO.fromPage(
                            trainingsplanRepository.findByDauerGreaterThanAndStatus(
                                    minDauer,
                                    enumStatus,
                                    pageable)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid parameter");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/trainingsplan/{id}")
    public ResponseEntity<Trainingsplan> getTrainingsplanById(@PathVariable String id) {

        // 🔒 ADMIN CHECK
        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return trainingsplanRepository.findById(id)
                .map(plan -> ResponseEntity.ok(plan))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/trainingsplan/{id}")
    public ResponseEntity<Void> deleteTrainingsplan(@PathVariable String id) {

        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!trainingsplanRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        trainingsplanRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
