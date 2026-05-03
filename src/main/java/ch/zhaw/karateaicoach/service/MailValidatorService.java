package ch.zhaw.karateaicoach.service;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.zhaw.karateaicoach.model.MailInformation;

@Service
public class MailValidatorService {
    private static final String DISIFY_BASE_URL = "https://disify.com";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);
    private static final String USER_AGENT = "Spring WebClient";
    private static final Logger logger = LoggerFactory.getLogger(MailValidatorService.class);

    private final WebClient webClient;

    public MailValidatorService() {
        this.webClient = WebClient.builder()
                .baseUrl(DISIFY_BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .filter(ServiceUtils.logRequest(logger))
                .build();
    }

    public MailInformation validateEmail(String email) {
        return webClient
                .get()
                .uri("/api/email/{email}", email)
                .retrieve()
                .bodyToMono(MailInformation.class)
                .block(REQUEST_TIMEOUT);
    }

    public boolean isValid(MailInformation info) {
        return info != null && info.isFormat() && !info.isDisposable() && info.isDns();
    }
}
