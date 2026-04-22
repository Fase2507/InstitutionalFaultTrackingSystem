package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Role;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.RoleDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.UserDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.security.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class RoleBootstrapService {

    private static final Logger log = LoggerFactory.getLogger(RoleBootstrapService.class);

    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TECHNICIAN = "TECHNICIAN";

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final Environment env;

    public RoleBootstrapService(RoleDao roleDao, UserDao userDao, Environment env) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.env = env;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureBaseRolesAndAdmin() {
        ensureRole(ROLE_USER);
        ensureRole(ROLE_ADMIN);
        ensureRole(ROLE_TECHNICIAN);
        ensureAdminUser();
    }

    public List<String> supportedRoles() {
        return List.of(ROLE_USER, ROLE_ADMIN, ROLE_TECHNICIAN);
    }

    public Role requireRole(String roleName) {
        String normalized = normalizeRole(roleName);
        return roleDao.findByRoleName(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Gecersiz rol: " + roleName));
    }

    private Role ensureRole(String roleName) {
        return roleDao.findByRoleName(roleName)
                .orElseGet(() -> {
                    Role role = Role.builder().roleName(roleName).build();
                    roleDao.save(role);
                    return role;
                });
    }

    private void ensureAdminUser() {
        String adminEmail = env.getProperty("app.admin.email", "admin@gmail.com").trim().toLowerCase(Locale.ROOT);
        Role adminRole = requireRole(ROLE_ADMIN);

        userDao.findByEmail(adminEmail).ifPresent(existing -> {
            if (!ROLE_ADMIN.equals(existing.getRole().getRoleName())) {
                existing.setRole(adminRole);
            }
            existing.setEnabled(true);
            if (existing.getEmailVerifiedAt() == null) {
                existing.setEmailVerifiedAt(LocalDateTime.now());
            }
            userDao.merge(existing);
        });

        if (userDao.findByEmail(adminEmail).isPresent()) {
            return;
        }

        String adminPassword = env.getProperty("app.admin.password", "").trim();
        if (adminPassword.isEmpty()) {
            log.warn("Admin kullanicisi olusturulmadi. APP_ADMIN_PASSWORD tanimli degil.");
            return;
        }

        User admin = User.builder()
                .firstName(env.getProperty("app.admin.firstName", "System"))
                .lastName(env.getProperty("app.admin.lastName", "Admin"))
                .email(adminEmail)
                .password(PasswordHasher.hash(adminPassword.toCharArray()))
                .enabled(true)
                .emailVerifiedAt(LocalDateTime.now())
                .role(adminRole)
                .build();
        userDao.save(admin);
    }

    private String normalizeRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Rol zorunlu.");
        }
        return roleName.trim().toUpperCase(Locale.ROOT);
    }
}
