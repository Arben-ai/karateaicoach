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
import ch.zhaw.karateaicoach.repository.TrainingsfokusRepository;
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
    TrainingsfokusRepository trainingsfokusRepository;

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

    @PostMapping("/trainingsplan/aus-fokus/{fokusId}")
    public ResponseEntity<Trainingsplan> createPlanFromFokus(@PathVariable String fokusId) {
        String userId = userService.getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        return trainingsfokusRepository.findById(fokusId)
                .map(fokus -> {
                    // Return existing plan if already generated for this fokus
                    java.util.Optional<Trainingsplan> existing = trainingsplanRepository.findByTrainingsfokusId(fokusId);
                    if (existing.isPresent()) return ResponseEntity.ok(existing.get());

                    String email = userService.getCurrentUserEmail();
                    String name = userService.getCurrentUserName();

                    return sportlerService.resolveCurrentSportler(userId, email, name)
                            .filter(sportler -> sportler.getId().equals(fokus.getSportlerId()))
                            .map(sportler -> {
                                String dauerInfo = fokus.getDauerWochen() != null
                                    ? fokus.getDauerWochen() + " Wochen" : "mehrere Wochen";
                                String einheitenInfo = fokus.getEinheitenProWoche() != null
                                    ? fokus.getEinheitenProWoche() + "x pro Woche" : "";
                                String minutenInfo = fokus.getMinutenProEinheit() != null
                                    ? fokus.getMinutenProEinheit() + " Minuten pro Einheit" : "";
                                String turnierInfo = (fokus.getTurniername() != null && fokus.getTurnierdatum() != null)
                                    ? "Zielturnier: " + fokus.getTurniername() + " am " + fokus.getTurnierdatum() : "";
                                String guertelInfo = sportler.getGuertelgrad() != null
                                    ? "Gürtelgrad: " + sportler.getGuertelgrad() : "";
                                String gewichtInfo = sportler.getGewicht() > 0
                                    ? "Gewicht: " + sportler.getGewicht() + " kg" : "";
                                String notizInfo = fokus.getNotiz() != null && !fokus.getNotiz().isBlank()
                                    ? "Coach-Anweisung: " + fokus.getNotiz() : "";

                                String prompt = String.format(
                                    "Erstelle einen detaillierten, wettkampforientierten Karate-Trainingsplan für %s.\n" +
                                    "Kategorie: %s\nSchwerpunkt: %s\nPlandauer: %s\n%s\n%s\n%s\n%s\n%s\n%s\n\n" +
                                    "WICHTIG – Formatierung:\n" +
                                    "- Verwende ### (3 Rauten) für Hauptabschnitte (z.B. ### Woche 1, ### Warm-up)\n" +
                                    "- Verwende #### (4 Rauten) für Unterabschnitte\n" +
                                    "- Verwende Bullet-Listen für Übungen\n" +
                                    "- Keine plain-text Überschriften ohne #-Zeichen\n" +
                                    "- Strukturiere jede Einheit mit Aufwärmen, Hauptteil und Cooldown",
                                    sportler.getName(),
                                    fokus.getKategorie() != null ? fokus.getKategorie() : "",
                                    fokus.getSchwerpunkt(),
                                    dauerInfo,
                                    einheitenInfo,
                                    minutenInfo,
                                    turnierInfo,
                                    guertelInfo,
                                    gewichtInfo,
                                    notizInfo);

                                String inhalt = chatModel.call(new Prompt(prompt))
                                        .getResult().getOutput().getText();

                                Trainingsplan plan = new Trainingsplan(
                                        "KI-Plan: " + fokus.getSchwerpunkt(),
                                        60,
                                        TrainingsplanStatus.ACTIVE,
                                        sportler.getId());
                                plan.setFokus(fokus.getSchwerpunkt());
                                plan.setInhalt(inhalt);
                                plan.setTrainingsfokusId(fokusId);

                                return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(trainingsplanRepository.save(plan));
                            })
                            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).<Trainingsplan>body(null));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/trainingsplan/me")
    public ResponseEntity<?> getMyTrainingsplan(
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
            @RequestParam(defaultValue = "") String sportlerName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (!userService.userHasRole("admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest().body("Invalid pagination parameters");
            }

            Pageable pageable = PageRequest.of(
                    page, size,
                    Sort.by("erstelldatum").descending().and(Sort.by("id").ascending()));

            boolean hasName = !sportlerName.isBlank();
            boolean hasStatus = status != null && !status.isBlank();
            boolean hasDauer = minDauer != null;

            if (hasName) {
                java.util.List<String> ids = sportlerRepository
                        .findByNameContainingIgnoreCase(sportlerName,
                                PageRequest.of(0, 200, Sort.by("name")))
                        .stream().map(s -> s.getId()).toList();
                if (ids.isEmpty()) {
                    return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                            org.springframework.data.domain.Page.empty(pageable)));
                }
                if (hasStatus) {
                    return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                            trainingsplanRepository.findByStatusAndSportlerIdIn(
                                    TrainingsplanStatus.valueOf(status), ids, pageable)));
                }
                return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                        trainingsplanRepository.findBySportlerIdIn(ids, pageable)));
            }

            if (hasDauer && hasStatus) {
                return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                        trainingsplanRepository.findByDauerGreaterThanAndStatus(
                                minDauer, TrainingsplanStatus.valueOf(status), pageable)));
            }

            if (hasDauer) {
                return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                        trainingsplanRepository.findByDauerGreaterThan(minDauer, pageable)));
            }

            if (hasStatus) {
                return ResponseEntity.ok(PaginatedResponseDTO.fromPage(
                        trainingsplanRepository.findByStatus(TrainingsplanStatus.valueOf(status), pageable)));
            }

            return ResponseEntity.ok(
                    PaginatedResponseDTO.fromPage(trainingsplanRepository.findAll(pageable)));

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
