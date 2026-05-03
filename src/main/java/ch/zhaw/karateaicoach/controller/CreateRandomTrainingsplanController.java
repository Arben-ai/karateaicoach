package ch.zhaw.karateaicoach.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CreateRandomTrainingsplanController {

    private final ChatClient chatClient;

    public CreateRandomTrainingsplanController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/trainingsplan/random")
    public ResponseEntity<String> createRandom() {
        String result = chatClient.prompt()
                .user("Erstelle einen zufälligen Karate-Trainingsplan mit einem passenden Titel, " +
                      "einer Dauer zwischen 30 und 90 Minuten und einem Sportler aus der Datenbank. " +
                      "Falls keine Sportler vorhanden sind, erstelle zuerst einen. " +
                      "Nutze dazu die verfügbaren Tools.")
                .call()
                .content();
        return ResponseEntity.ok(result);
    }
}
