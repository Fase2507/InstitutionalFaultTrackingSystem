package tr.duzce.edu.mf.bm.KurumArizaTakip.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.TechnicianProfile;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.AdminService;
import tr.duzce.edu.mf.bm.KurumArizaTakip.web.dto.AdminDtos.RoleUpdateRequest;
import tr.duzce.edu.mf.bm.KurumArizaTakip.web.dto.AdminDtos.TechnicianCategoryUpdateRequest;
import tr.duzce.edu.mf.bm.KurumArizaTakip.web.dto.AdminDtos.TechnicianProfileResponse;
import tr.duzce.edu.mf.bm.KurumArizaTakip.web.dto.AdminDtos.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers(HttpSession session) {
        Long adminUserId = requireSessionUserId(session);
        return adminService.listUsers(adminUserId).stream()
                .map(this::toUserResponse)
                .toList();
    }

    @PatchMapping("/users/{userId}/role")
    public UserResponse assignRole(@PathVariable Long userId,
                                   @RequestBody RoleUpdateRequest request,
                                   HttpSession session) {
        Long adminUserId = requireSessionUserId(session);
        return toUserResponse(adminService.assignRole(adminUserId, userId, request.roleName()));
    }

    @PutMapping("/technicians/{userId}/categories")
    public TechnicianProfileResponse assignTechnicianCategories(@PathVariable Long userId,
                                                                @RequestBody TechnicianCategoryUpdateRequest request,
                                                                HttpSession session) {
        Long adminUserId = requireSessionUserId(session);
        TechnicianProfile profile = adminService.assignTechnicianCategories(
                adminUserId,
                userId,
                request.categoryIds(),
                Boolean.TRUE.equals(request.active())
        );
        return new TechnicianProfileResponse(
                profile.getUser().getId(),
                profile.isActive(),
                profile.getCategories().stream().map(category -> category.getId()).toList()
        );
    }

    private Long requireSessionUserId(HttpSession session) {
        Long userId = SessionAuth.getUserId(session);
        if (userId == null) {
            throw new IllegalStateException("Bu islem icin giris yapmalisiniz.");
        }
        return userId;
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isEnabled(),
                user.getRole().getRoleName()
        );
    }
}
