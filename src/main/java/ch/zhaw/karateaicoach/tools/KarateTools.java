package ch.zhaw.karateaicoach.tools;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.zhaw.karateaicoach.model.Sportler;
import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;

@Component
public class KarateTools {

    @Autowired
    private SportlerService sportlerService;

    @Autowired
    private TrainingsplanRepository trainingsplanRepository;

    @Tool(description = "Gibt alle Sportler aus der Datenbank zurück.")
    public List<Sportler> getAllSportler() {
        return sportlerService.getAllSportler();
    }

    @Tool(description = "Gibt alle Trainingspläne aus der Datenbank zurück.")
    public List<Trainingsplan> getAllTrainingsplaene() {
        return trainingsplanRepository.findAll();
    }

    @Tool(description = "Erstellt einen neuen Sportler mit Name und E-Mail.")
    public void createSportler(String name, String email) {
        sportlerService.createSportler(name, email);
    }

    @Tool(description = "Erstellt einen neuen Trainingsplan. Benötigt Titel, Dauer in Minuten und den Namen des Sportlers. Falls der Sportler noch nicht existiert, wird er zuerst erstellt.")
    public void createTrainingsplan(String titel, int dauer, String sportlerName) {
        Sportler sportler = sportlerService.getAllSportler().stream()
                .filter(s -> s.getName().equalsIgnoreCase(sportlerName))
                .findFirst()
                .orElseGet(() -> {
                    sportlerService.createSportler(sportlerName, "");
                    return sportlerService.getAllSportler().stream()
                            .filter(s -> s.getName().equalsIgnoreCase(sportlerName))
                            .findFirst()
                            .orElseThrow();
                });

        Trainingsplan plan = new Trainingsplan(titel, dauer, TrainingsplanStatus.DRAFT, sportler.getId());
        trainingsplanRepository.save(plan);
    }
}
