package ch.zhaw.karateaicoach.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("trainingsfokus")
public class Trainingsfokus {

    @Id
    private String id;

    private String beschreibung;
    private String schwerpunkt;
    private TrainingsfokusStatus status;
    private String sportlerId;

    public Trainingsfokus() {}

    public Trainingsfokus(String beschreibung, String schwerpunkt, TrainingsfokusStatus status, String sportlerId) {
        this.beschreibung = beschreibung;
        this.schwerpunkt = schwerpunkt;
        this.status = status;
        this.sportlerId = sportlerId;
    }

    public String getId() { return id; }
    public String getBeschreibung() { return beschreibung; }
    public String getSchwerpunkt() { return schwerpunkt; }
    public TrainingsfokusStatus getStatus() { return status; }
    public String getSportlerId() { return sportlerId; }

    public void setStatus(TrainingsfokusStatus status) { this.status = status; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public void setSchwerpunkt(String schwerpunkt) { this.schwerpunkt = schwerpunkt; }
}
