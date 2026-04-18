package ch.zhaw.karateaicoach.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sportler")
public class Sportler {

    @Id
    private String id;

    private String name;
    private String email;
    private String guertelgrad;
    private double gewicht;
    private String userId;

    public Sportler() {}

    public Sportler(String name, String email, String guertelgrad, double gewicht) {
        this.name = name;
        this.email = email;
        this.guertelgrad = guertelgrad;
        this.gewicht = gewicht;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getGuertelgrad() { return guertelgrad; }
    public double getGewicht() { return gewicht; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}