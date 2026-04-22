package tr.duzce.edu.mf.bm.KurumArizaTakip.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.CategoryCatalogService;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.RoleBootstrapService;

@Component
public class ApplicationBootstrap {

    private final RoleBootstrapService roleBootstrapService;
    private final CategoryCatalogService categoryCatalogService;

    public ApplicationBootstrap(RoleBootstrapService roleBootstrapService,
                                CategoryCatalogService categoryCatalogService) {
        this.roleBootstrapService = roleBootstrapService;
        this.categoryCatalogService = categoryCatalogService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        roleBootstrapService.ensureBaseRolesAndAdmin();
        categoryCatalogService.ensureDefaultsAndList();
    }
}
