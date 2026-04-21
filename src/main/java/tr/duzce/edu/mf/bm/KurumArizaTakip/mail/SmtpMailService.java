package tr.duzce.edu.mf.bm.KurumArizaTakip.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Primary
@Log4j2
public class SmtpMailService implements MailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    public SmtpMailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = env.getProperty("mail.from", "noreply@kurumariza.edu.tr");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Hesap Doğrulama Kodu");

            String content = String.format(
                    "<h3>Merhaba,</h3>" +
                            "<p>Kurum Arıza Takip Sistemine hoş geldiniz.</p>" +
                            "<p>Hesabınızı doğrulamak için aşağıdaki kodu kullanabilirsiniz:</p>" +
                            "<h2 style='color: #2e6c80;'>%s</h2>" +
                            "<p>Bu kod 10 dakika geçerlidir.</p>" +
                            "<br>" +
                            "<p>İyi çalışmalar dileriz.</p>",
                    code
            );

            helper.setText(content, true);

            log.info("Doğrulama kodu gönderiliyor: {}", toEmail);
            mailSender.send(message);
            log.info("Doğrulama kodu başarıyla gönderildi: {}", toEmail);

        } catch (MessagingException e) {
            log.error("E-posta gönderme hatası: {}", e.getMessage());
            throw new RuntimeException("E-posta gönderilirken bir hata oluştu.", e);
        }
    }
}
