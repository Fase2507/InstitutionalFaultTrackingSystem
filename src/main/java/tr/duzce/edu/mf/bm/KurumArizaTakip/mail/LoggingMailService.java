package tr.duzce.edu.mf.bm.KurumArizaTakip.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(LoggingMailService.class);

    @Override
    public void sendVerificationCode(String toEmail, String code) {
        log.info("MAIL_SEND to={} type=VERIFICATION_CODE code={}", toEmail, code);
    }
}

