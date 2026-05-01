package ch.zhaw.karateaicoach.model;

public class TrainingsfokusCreateDTO {

    private String beschreibung;
    private String schwerpunkt;
    private String kategorie;
    private String notiz;
    private TrainingsfokusStatus status;
    private String sportlerId;
    private Integer dauerWochen;
    private Integer einheitenProWoche;
    private Integer minutenProEinheit;

    public TrainingsfokusCreateDTO() {}

    public String getBeschreibung() { return beschreibung; }
    public String getSchwerpunkt() { return schwerpunkt; }
    public String getKategorie() { return kategorie; }
    public String getNotiz() { return notiz; }
    public TrainingsfokusStatus getStatus() { return status; }
    public String getSportlerId() { return sportlerId; }
    public Integer getDauerWochen() { return dauerWochen; }
    public Integer getEinheitenProWoche() { return einheitenProWoche; }
    public Integer getMinutenProEinheit() { return minutenProEinheit; }
    private String turniername;
    private String turnierdatum;
    public String getTurniername() { return turniername; }
    public String getTurnierdatum() { return turnierdatum; }
}
