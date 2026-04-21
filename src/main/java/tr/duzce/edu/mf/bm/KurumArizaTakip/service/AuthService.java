package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Role;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.VerificationToken;
import tr.duzce.edu.mf.bm.KurumArizaTakip.mail.MailService;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.RoleDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.UserDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.VerificationTokenDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.security.PasswordHasher;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.security.VerificationCodeHasher;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final String ALLOWED_DOMAIN = "@gmail.com";

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final VerificationTokenDao verificationTokenDao;
    private final MailService mailService;

    public AuthService(UserDao userDao,
                       RoleDao roleDao,
                       VerificationTokenDao verificationTokenDao,
                       MailService mailService) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.verificationTokenDao = verificationTokenDao;
        this.mailService = mailService;
    }

    @Transactional
    public User register(String firstName, String lastName, String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        requireAllowedDomain(normalizedEmail);

        userDao.findByEmail(normalizedEmail).ifPresent(u -> {
            throw new IllegalArgumentException("Bu email zaten kayıtlı.");
        });

        Role userRole = roleDao.findByRoleName("USER").orElseGet(() -> {
            Role created = Role.builder().roleName("USER").build();
            roleDao.save(created);
            return created;
        });

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(normalizedEmail)
                .password(PasswordHasher.hash(rawPassword.toCharArray()))
                .enabled(false)
                .role(userRole)
                .build();

        userDao.save(user);

        String code = generateNumericCode(6);
        VerificationToken token = VerificationToken.builder()
                .user(user)
                .codeHash(VerificationCodeHasher.sha256(code))
                .sentAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .attemptCount(0)
                .build();
        verificationTokenDao.save(token);

        mailService.sendVerificationCode(normalizedEmail, code);
        return user;
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        User user = userDao.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));

        LocalDateTime now = LocalDateTime.now();
        List<VerificationToken> active = verificationTokenDao.findActiveByUserId(user.getId(), now);
        if (active.isEmpty()) {
            throw new IllegalArgumentException("Geçerli doğrulama kodu bulunamadı. Yeni kod isteyin.");
        }

        VerificationToken token = active.get(0);
        token.setAttemptCount(token.getAttemptCount() + 1);

        String expected = token.getCodeHash();
        String actual = VerificationCodeHasher.sha256(code);
        if (!expected.equalsIgnoreCase(actual)) {
            verificationTokenDao.merge(token);
            throw new IllegalArgumentException("Doğrulama kodu hatalı.");
        }

        token.setUsedAt(now);
        verificationTokenDao.merge(token);

        user.setEnabled(true);
        user.setEmailVerifiedAt(now);
        userDao.merge(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(String email, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = userDao.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Email veya şifre hatalı."));

        boolean ok = PasswordHasher.verify(rawPassword.toCharArray(), user.getPassword());
        if (!ok) {
            throw new IllegalArgumentException("Email veya şifre hatalı.");
        }
        if (!user.isEnabled()) {
            throw new IllegalStateException("Hesap aktif değil. Email doğrulaması gerekli.");
        }
        return user;
    }

    private static void requireAllowedDomain(String email) {
        if (email == null) throw new IllegalArgumentException("Email zorunlu.");
        if (!email.endsWith(ALLOWED_DOMAIN)) {
            throw new IllegalArgumentException("Sadece " + ALLOWED_DOMAIN + " uzantılı mailler kabul edilir.");
        }
    }

    private static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static String generateNumericCode(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int value = RNG.nextInt(max - min + 1) + min;
        return Integer.toString(value);
    }
}

