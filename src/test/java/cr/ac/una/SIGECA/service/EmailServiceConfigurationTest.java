package cr.ac.una.SIGECA.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class EmailServiceConfigurationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testSendVerificationEmail_ThrowsExceptionWhenNotConfigured() {
        // This test assumes application.properties is not properly configured with a valid email
        assertThrows(IllegalStateException.class, () -> {
            emailService.sendVerificationEmail("test@example.com", "TestUser", "123456");
        });
    }
}
