package tr.duzce.edu.mf.bm.KurumArizaTakip.mail;

public interface MailService {
    void sendVerificationCode(String toEmail, String code);
}

