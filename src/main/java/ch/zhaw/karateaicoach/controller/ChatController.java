package ch.zhaw.karateaicoach.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.karateaicoach.model.Trainingsplan;
import ch.zhaw.karateaicoach.model.TrainingsplanStatus;
import ch.zhaw.karateaicoach.repository.TrainingsplanRepository;
import ch.zhaw.karateaicoach.service.SportlerService;
import ch.zhaw.karateaicoach.service.UserService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final UserService userService;
    private final SportlerService sportlerService;
    private final TrainingsplanRepository trainingsplanRepository;

    public ChatController(ChatClient chatClient, UserService userService,
            SportlerService sportlerService, TrainingsplanRepository trainingsplanRepository) {
        this.chatClient = chatClient;
        this.userService = userService;
        this.sportlerService = sportlerService;
        this.trainingsplanRepository = trainingsplanRepository;
    }

    @PostMapping
    public String chat(@RequestBody String message) {
        String reply = chatClient.prompt()
                .user(message)
                .call()
                .content();

        if (message.contains("Trainingsplan") && message.contains("Schwerpunkt:")) {
            String userId = userService.getCurrentUserId();
            String email = userService.getCurrentUserEmail();
            if (userId != null) {
                sportlerService.resolveCurrentSportler(userId, email, null).ifPresent(sportler -> {
                    String schwerpunkt = extractSchwerpunkt(message);
                    Trainingsplan plan = new Trainingsplan(
                            "KI-Plan: " + schwerpunkt,
                            60,
                            TrainingsplanStatus.ACTIVE,
                            sportler.getId());
                    plan.setFokus(schwerpunkt);
                    plan.setInhalt(reply);
                    trainingsplanRepository.save(plan);
                });
            }
        }

        return reply;
    }

    private String extractSchwerpunkt(String message) {
        String marker = "Schwerpunkt:";
        int idx = message.indexOf(marker);
        if (idx == -1) return "Karate";
        String after = message.substring(idx + marker.length()).trim();
        String line = after.split("[\n\\r]")[0].trim();
        return line.isEmpty() ? "Karate" : line;
    }
}
