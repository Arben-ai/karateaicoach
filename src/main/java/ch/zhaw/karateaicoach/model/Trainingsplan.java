package ch.zhaw.karateaicoach.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("trainingsplan")
public class Trainingsplan {

    @Id
    private String id;

    private String titel;
    private LocalDate erstelldatum;
    private int dauer;
    private TrainingsplanStatus status;
    private String sportlerId;

    public Trainingsplan() {
    }

    public Trainingsplan(String titel, int dauer, TrainingsplanStatus status, String sportlerId) {
        this.titel = titel;
        this.dauer = dauer;
        this.status = status;
        this.sportlerId = sportlerId;
        this.erstelldatum = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public LocalDate getErstelldatum() {
        return erstelldatum;
    }

    public int getDauer() {
        return dauer;
    }

    @org.springframework.data.mongodb.core.mapping.Field("status")
    public TrainingsplanStatus getStatus() {
        return status;
    }

    public String getSportlerId() {
        return sportlerId;
    }

    public void setStatus(TrainingsplanStatus status) {
        this.status = status;
    }

}