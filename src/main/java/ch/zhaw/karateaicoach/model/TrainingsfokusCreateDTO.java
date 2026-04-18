package ch.zhaw.karateaicoach.model;

public class TrainingsfokusCreateDTO {

    private String beschreibung;
    private String schwerpunkt;
    private TrainingsfokusStatus status;
    private String sportlerId;

    public TrainingsfokusCreateDTO() {}

    public String getBeschreibung() { return beschreibung; }
    public String getSchwerpunkt() { return schwerpunkt; }
    public TrainingsfokusStatus getStatus() { return status; }
    public String getSportlerId() { return sportlerId; }
}
