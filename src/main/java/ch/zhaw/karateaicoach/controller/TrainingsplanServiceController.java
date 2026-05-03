package ch.zhaw.karateaicoach.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.zhaw.karateaicoach.model.Mail;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusAggregationDTO;
import ch.zhaw.karateaicoach.model.TrainingsplanStatusChangeDTO;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.MailService;
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

    @Autowired
    private MailService mailService;

    @PutMapping("/activateTrainingsplan")
    public ResponseEntity<Trainingsplan> activateTrainingsplan(
            @RequestBody TrainingsplanStatusChangeDTO dto) {

        Optional<Trainingsplan> plan =
                trainingsplanService.activateTrainingsplan(dto.getTrainingsplanId());

        if (plan.isPresent()) {
            Trainingsplan t = plan.get();
            sportlerService.getSportlerById(t.getSportlerId()).ifPresent(sportler -> {
                Mail mail = new Mail();
                mail.setTo(sportler.getEmail());
                mail.setSubject("Trainingsplan aktiviert: " + t.getTitel());
                mail.setMessage("Hallo " + sportler.getName() + ",\n\n"
                        + "dein Trainingsplan \"" + t.getTitel() + "\" wurde aktiviert.\n"
                        + "Status: " + t.getStatus() + "\n\n"
                        + "Viel Erfolg beim Training!\n"
                        + "Dein KarateAI Coach");
                mailService.sendMail(mail);
            });
            return new ResponseEntity<>(t, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/completeTrainingsplan")
    public ResponseEntity<Trainingsplan> completeTrainingsplan(
            @RequestBody TrainingsplanStatusChangeDTO dto) {

        Optional<Trainingsplan> plan =
                trainingsplanService.completeTrainingsplan(dto.getTrainingsplanId());

        if (plan.isPresent()) {
            Trainingsplan t = plan.get();
            sportlerService.getSportlerById(t.getSportlerId()).ifPresent(sportler -> {
                Mail mail = new Mail();
                mail.setTo(sportler.getEmail());
                mail.setSubject("Trainingsplan abgeschlossen: " + t.getTitel());
                mail.setMessage("Hallo " + sportler.getName() + ",\n\n"
                        + "dein Trainingsplan \"" + t.getTitel() + "\" wurde abgeschlossen.\n"
                        + "Status: " + t.getStatus() + "\n\n"
                        + "Herzlichen Glückwunsch!\n"
                        + "Dein KarateAI Coach");
                mailService.sendMail(mail);
            });
            return new ResponseEntity<>(t, HttpStatus.OK);
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