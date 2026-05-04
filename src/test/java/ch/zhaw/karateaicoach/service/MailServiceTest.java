package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.Mail;

class MailServiceTest {

    private JavaMailSender mailSender;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailService();
        ReflectionTestUtils.setField(mailService, "mailSender", mailSender);
    }

    @Test
    void sendMail_returnsTrue_whenMailSentSuccessfully() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        Mail mail = new Mail();
        mail.setTo("user@test.com");
        mail.setSubject("Test Subject");
        mail.setMessage("Test message");

        assertTrue(mailService.sendMail(mail));
    }

    @Test
    void sendMail_returnsFalse_whenExceptionThrown() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        Mail mail = new Mail();
        mail.setTo("user@test.com");
        mail.setSubject("Test Subject");
        mail.setMessage("Test message");

        assertFalse(mailService.sendMail(mail));
    }
}
