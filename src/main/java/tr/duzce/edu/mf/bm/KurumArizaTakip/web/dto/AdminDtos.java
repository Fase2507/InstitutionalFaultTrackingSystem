package tr.duzce.edu.mf.bm.KurumArizaTakip.web.dto;

import java.util.List;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record RoleUpdateRequest(String roleName) {
    }

    public record TechnicianCategoryUpdateRequest(List<Long> categoryIds, Boolean active) {
    }

    public record UserResponse(Long id,
                               String firstName,
                               String lastName,
                               String email,
                               boolean enabled,
                               String roleName) {
    }

    public record TechnicianProfileResponse(Long userId,
                                            boolean active,
                                            List<Long> categoryIds) {
    }
}
