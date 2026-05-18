package cr.ac.una.SIGECA.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        configureSender(helper);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        if (attachment != null) {
            helper.addAttachment(attachmentName, new ByteArrayResource(attachment));
        }

        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String userName, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        configureSender(helper);
        helper.setTo(to);
        helper.setSubject("Código de Verificación SIGECA");
        helper.setText("Hola " + userName + ",\n\nGracias por registrarte en SIGECA.\n\nTu código de verificación es: " + code + "\n\nPor favor ingrésalo en el sistema para activar tu cuenta.");

        mailSender.send(message);
    }
    private void configureSender(MimeMessageHelper helper) throws MessagingException {
        if (mailFrom == null || mailFrom.isBlank() || mailFrom.contains("tu-correo@")) {
            throw new IllegalStateException("Configura spring.mail.username con un correo real antes de enviar mensajes.");
        }
        helper.setFrom(mailFrom);
    }
}
