package ch.zhaw.karateaicoach.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusAggregationDTO;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusChangeDTO;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.TrainingsplanService;

@RestController
@RequestMapping("/api/service")
public class TrainingsplanServiceController {

    @Autowired
    private TrainingsplanService trainingsplanService;

    @Autowired
    private TrainingsplanRepository trainingsplanRepository;

    @Autowired
    private SportlerService sportlerService;

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

    @GetMapping("/trainingsplanDashboard")
    public ResponseEntity<List<TrainingsplanStatusAggregationDTO>> getTrainingsplanDashboard(
            @RequestParam String sportlerId) {

        if (!sportlerService.sportlerExists(sportlerId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<TrainingsplanStatusAggregationDTO> result =
                trainingsplanRepository.getTrainingsplanStatusAggregation(sportlerId);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}