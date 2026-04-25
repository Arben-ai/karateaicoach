package ch.zhaw.karateaicoach.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("trainingsfokus")
public class Trainingsfokus {

    @Id
    private String id;

    private String beschreibung;
    private String schwerpunkt;
    private String kategorie;
    private String notiz;
    private TrainingsfokusStatus status;
    private String sportlerId;
    private boolean gelesen = false;
    private java.time.Instant createdAt;

    public Trainingsfokus() {}

    public Trainingsfokus(String beschreibung, String schwerpunkt, String kategorie, String notiz, TrainingsfokusStatus status, String sportlerId) {
        this.beschreibung = beschreibung;
        this.schwerpunkt = schwerpunkt;
        this.kategorie = kategorie;
        this.notiz = notiz;
        this.status = status;
        this.sportlerId = sportlerId;
        this.createdAt = java.time.Instant.now();
    }

    public String getId() { return id; }
    public String getBeschreibung() { return beschreibung; }
    public String getSchwerpunkt() { return schwerpunkt; }
    public String getKategorie() { return kategorie; }
    public String getNotiz() { return notiz; }
    public TrainingsfokusStatus getStatus() { return status; }
    public String getSportlerId() { return sportlerId; }
    public boolean isGelesen() { return gelesen; }
    public java.time.Instant getCreatedAt() { return createdAt; }

    public void setStatus(TrainingsfokusStatus status) { this.status = status; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    public void setSchwerpunkt(String schwerpunkt) { this.schwerpunkt = schwerpunkt; }
    public void setKategorie(String kategorie) { this.kategorie = kategorie; }
    public void setNotiz(String notiz) { this.notiz = notiz; }
    public void setGelesen(boolean gelesen) { this.gelesen = gelesen; }
}
