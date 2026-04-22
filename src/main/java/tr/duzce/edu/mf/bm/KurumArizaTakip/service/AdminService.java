package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Category;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.TechnicianProfile;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.CategoryDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.TechnicianProfileDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.UserDao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final TechnicianProfileDao technicianProfileDao;
    private final RoleBootstrapService roleBootstrapService;

    public AdminService(UserDao userDao,
                        CategoryDao categoryDao,
                        TechnicianProfileDao technicianProfileDao,
                        RoleBootstrapService roleBootstrapService) {
        this.userDao = userDao;
        this.categoryDao = categoryDao;
        this.technicianProfileDao = technicianProfileDao;
        this.roleBootstrapService = roleBootstrapService;
    }

    @Transactional(readOnly = true)
    public List<User> listUsers(Long adminUserId) {
        requireAdmin(adminUserId);
        return userDao.findAll();
    }

    @Transactional
    public User assignRole(Long adminUserId, Long targetUserId, String roleName) {
        requireAdmin(adminUserId);
        User target = requireUser(targetUserId);
        String normalizedRole = roleBootstrapService.requireRole(roleName).getRoleName();
        target.setRole(roleBootstrapService.requireRole(normalizedRole));
        User merged = userDao.merge(target);

        if (!RoleBootstrapService.ROLE_TECHNICIAN.equals(normalizedRole)) {
            technicianProfileDao.findByUserId(targetUserId).ifPresent(profile -> {
                profile.setActive(false);
                profile.getCategories().clear();
                technicianProfileDao.merge(profile);
            });
        } else {
            technicianProfileDao.findByUserId(targetUserId).orElseGet(() -> {
                TechnicianProfile profile = TechnicianProfile.builder()
                        .user(merged)
                        .active(true)
                        .build();
                technicianProfileDao.save(profile);
                return profile;
            });
        }

        return merged;
    }

    @Transactional
    public TechnicianProfile assignTechnicianCategories(Long adminUserId,
                                                        Long technicianUserId,
                                                        List<Long> categoryIds,
                                                        boolean active) {
        requireAdmin(adminUserId);
        User target = requireUser(technicianUserId);
        if (!RoleBootstrapService.ROLE_TECHNICIAN.equals(target.getRole().getRoleName())) {
            throw new IllegalStateException("Kategori atamak icin kullanici TECHNICIAN rolunde olmali.");
        }

        List<Category> categories = categoryDao.findAllByIds(categoryIds);
        if (categoryIds != null && categories.size() != new LinkedHashSet<>(categoryIds).size()) {
            throw new IllegalArgumentException("Bir veya daha fazla kategori bulunamadi.");
        }

        TechnicianProfile profile = technicianProfileDao.findByUserId(technicianUserId)
                .orElseGet(() -> {
                    TechnicianProfile created = TechnicianProfile.builder()
                            .user(target)
                            .active(active)
                            .build();
                    technicianProfileDao.save(created);
                    return created;
                });

        profile.setActive(active);
        Set<Category> assigned = new LinkedHashSet<>(categories);
        profile.getCategories().clear();
        profile.getCategories().addAll(assigned);
        return technicianProfileDao.merge(profile);
    }

    private User requireAdmin(Long adminUserId) {
        User admin = requireUser(adminUserId);
        if (!RoleBootstrapService.ROLE_ADMIN.equals(admin.getRole().getRoleName())) {
            throw new IllegalStateException("Bu islem icin ADMIN yetkisi gerekli.");
        }
        return admin;
    }

    private User requireUser(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi."));
    }
}
