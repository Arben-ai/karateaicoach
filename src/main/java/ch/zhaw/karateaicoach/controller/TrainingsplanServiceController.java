package ch.zhaw.karateaicoach.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusChangeDTO;
import ch.zhaw.karateaicoach.service.TrainingsplanService;

@RestController
@RequestMapping("/api/service")
public class TrainingsplanServiceController {

    @Autowired
    private TrainingsplanService trainingsplanService;

    @PutMapping("/activateTrainingsplan")
    public ResponseEntity<Trainingsplan> activateTrainingsplan(
            @RequestBody TrainingsplanStatusChangeDTO dto) {

        Optional<Trainingsplan> plan =
                trainingsplanService.activateTrainingsplan(dto.getTrainingsplanId());

        if (plan.isPresent()) {
            return new ResponseEntity<>(plan.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // 🔥 NEU
    @PutMapping("/completeTrainingsplan")
    public ResponseEntity<Trainingsplan> completeTrainingsplan(
            @RequestBody TrainingsplanStatusChangeDTO dto) {

        Optional<Trainingsplan> plan =
                trainingsplanService.completeTrainingsplan(dto.getTrainingsplanId());

        if (plan.isPresent()) {
            return new ResponseEntity<>(plan.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}