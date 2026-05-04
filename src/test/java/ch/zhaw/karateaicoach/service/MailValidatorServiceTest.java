package ch.zhaw.karateaicoach.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import ch.zhaw.karateaicoach.model.MailInformation;

class MailValidatorServiceTest {

    private MailValidatorService mailValidatorService;

    @BeforeEach
    void setUp() {
        mailValidatorService = new MailValidatorService();
    }

    @Test
    void isValid_returnsFalse_whenNull() {
        assertFalse(mailValidatorService.isValid(null));
    }

    @Test
    void isValid_returnsFalse_whenFormatFalse() {
        MailInformation info = new MailInformation();
        ReflectionTestUtils.setField(info, "format", false);
        ReflectionTestUtils.setField(info, "disposable", false);
        ReflectionTestUtils.setField(info, "dns", true);

        assertFalse(mailValidatorService.isValid(info));
    }

    @Test
    void isValid_returnsFalse_whenDisposable() {
        MailInformation info = new MailInformation();
        ReflectionTestUtils.setField(info, "format", true);
        ReflectionTestUtils.setField(info, "disposable", true);
        ReflectionTestUtils.setField(info, "dns", true);

        assertFalse(mailValidatorService.isValid(info));
    }

    @Test
    void isValid_returnsFalse_whenDnsFalse() {
        MailInformation info = new MailInformation();
        ReflectionTestUtils.setField(info, "format", true);
        ReflectionTestUtils.setField(info, "disposable", false);
        ReflectionTestUtils.setField(info, "dns", false);

        assertFalse(mailValidatorService.isValid(info));
    }

    @Test
    void isValid_returnsTrue_whenAllValid() {
        MailInformation info = new MailInformation();
        ReflectionTestUtils.setField(info, "format", true);
        ReflectionTestUtils.setField(info, "disposable", false);
        ReflectionTestUtils.setField(info, "dns", true);

        assertTrue(mailValidatorService.isValid(info));
    }
}
