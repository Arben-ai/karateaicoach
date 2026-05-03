package ch.zhaw.karateaicoach.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.zhaw.karateaicoach.model.MailInformation;
import ch.zhaw.karateaicoach.model.PaginatedResponseDTO;
import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.SportlerCreateDTO;
import ch.zhaw.karateaicoach.repository.SportlerRepository;
import ch.zhaw.karateaicoach.service.MailValidatorService;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;

@RestController
@RequestMapping("/api")
public class SportlerController {

    private static final Logger logger = LoggerFactory.getLogger(SportlerController.class);

    @Autowired
    SportlerRepository sportlerRepository;

    @Autowired
    SportlerService sportlerService;

    @Autowired
    UserService userService;

    @Autowired
    MailValidatorService mailValidatorService;

    @PostMapping("/sportler")
    public ResponseEntity<Sportler> createSportler(@RequestBody SportlerCreateDTO dto) {
        MailInformation mailInfo = mailValidatorService.validateEmail(dto.getEmail());
        if (!mailValidatorService.isValid(mailInfo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (sportlerRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        if (sportlerRepository.existsByNameIgnoreCase(dto.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
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

    @PostMapping("/sportler/me")
    public ResponseEntity<Sportler> createMySportler(@RequestBody(required = false) java.util.Map<String, String> body) {
        String userId = userService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = userService.getCurrentUserEmail();
        if (email == null && body != null) {
            email = body.get("email");
        }
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        MailInformation mailInfo = mailValidatorService.validateEmail(email);
        if (!mailValidatorService.isValid(mailInfo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Optional<Sportler> existing = sportlerRepository.findByUserId(userId);
        if (existing.isPresent()) return ResponseEntity.ok(existing.get());
        Optional<Sportler> byEmail = sportlerRepository.findByEmailAndUserIdIsNull(email);
        if (byEmail.isPresent()) {
            Sportler s = byEmail.get();
            s.setUserId(userId);
            return ResponseEntity.ok(sportlerRepository.save(s));
        }
        String name = body != null ? body.getOrDefault("name", email) : email;
        String guertelgrad = body != null ? body.getOrDefault("guertelgrad", "") : "";
        double gewicht = 0;
        if (body != null && body.containsKey("gewicht")) {
            try { gewicht = Double.parseDouble(body.get("gewicht")); } catch (NumberFormatException e) {
                logger.warn("Invalid gewicht value: {}", body.get("gewicht"));
            }
        }
        Sportler sportler = new Sportler(name, email, guertelgrad, gewicht);
        sportler.setUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(sportlerRepository.save(sportler));
    }

    @GetMapping("/sportler/me")
    public ResponseEntity<Sportler> getMySportler() {
        String userId = userService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String email = userService.getCurrentUserEmail();
        String name = userService.getCurrentUserName();
        return sportlerService.resolveCurrentSportler(userId, email, name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PatchMapping("/sportler/me")
    public ResponseEntity<Sportler> updateMySportler(@RequestBody java.util.Map<String, Object> body) {
        String userId = userService.getCurrentUserId();
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        String email = userService.getCurrentUserEmail();
        String name = userService.getCurrentUserName();
        return sportlerService.resolveCurrentSportler(userId, email, name)
                .map(sportler -> {
                    if (body.containsKey("guertelgrad"))
                        sportler.setGuertelgrad((String) body.get("guertelgrad"));
                    if (body.containsKey("gewicht")) {
                        Object g = body.get("gewicht");
                        if (g instanceof Number) sportler.setGewicht(((Number) g).doubleValue());
                    }
                    return ResponseEntity.ok(sportlerRepository.save(sportler));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/sportler")
    public ResponseEntity<?> getAllSportler(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String guertelgrad) {
        try {
            if (page < 0 || size < 1) {
                return ResponseEntity.badRequest().body("Invalid pagination parameters");
            }

            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by("name").ascending().and(Sort.by("id").ascending()));

            boolean hasSearch = !search.isBlank();
            boolean hasGuertelgrad = !guertelgrad.isBlank();

            org.springframework.data.domain.Page<Sportler> result;
            if (hasSearch && hasGuertelgrad) {
                result = sportlerRepository.findByNameContainingIgnoreCaseAndGuertelgradIgnoreCase(search, guertelgrad, pageable);
            } else if (hasSearch) {
                result = sportlerRepository.findByNameContainingIgnoreCase(search, pageable);
            } else if (hasGuertelgrad) {
                result = sportlerRepository.findByGuertelgradIgnoreCase(guertelgrad, pageable);
            } else {
                result = sportlerRepository.findAll(pageable);
            }

            return ResponseEntity.ok(PaginatedResponseDTO.fromPage(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/sportler/{id}")
    public ResponseEntity<Sportler> getSportlerById(@PathVariable String id) {
        return sportlerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/sportler/{id}")
    public ResponseEntity<Void> deleteSportler(@PathVariable String id) {
        return sportlerRepository.findById(id).map(sportler -> {
            String currentUserId = userService.getCurrentUserId();
            boolean isOwner = currentUserId != null && currentUserId.equals(sportler.getUserId());
            boolean isAdmin = userService.userHasRole("admin");

            if (!isOwner && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            }

            sportlerRepository.deleteById(id);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
