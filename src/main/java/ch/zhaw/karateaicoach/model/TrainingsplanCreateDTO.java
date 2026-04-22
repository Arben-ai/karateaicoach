package ch.zhaw.karateaicoach.model;

public class TrainingsplanCreateDTO {

    private String titel;
    private int dauer;
    private TrainingsplanStatus status;
    private String sportlerId;
    private String fokus;

    public TrainingsplanCreateDTO() {}

    public String getTitel() { return titel; }
    public int getDauer() { return dauer; }
    public TrainingsplanStatus getStatus() { return status; }
    public String getSportlerId() { return sportlerId; }
    public String getFokus() { return fokus; }
}