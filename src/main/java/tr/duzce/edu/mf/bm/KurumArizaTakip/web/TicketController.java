package tr.duzce.edu.mf.bm.KurumArizaTakip.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.CategoryCatalogService;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.TicketService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CategoryCatalogService categoryCatalogService;

    public TicketController(TicketService ticketService,
                            CategoryCatalogService categoryCatalogService) {
        this.ticketService = ticketService;
        this.categoryCatalogService = categoryCatalogService;
    }

    @GetMapping
    public String list(Model model) {
        try {
            model.addAttribute("tickets", ticketService.listTickets());
        } catch (Exception ex) {
            model.addAttribute("tickets", java.util.List.of());
            model.addAttribute("error", ex.getMessage());
        }
        return "tickets/list";
    }

    @GetMapping("/new")
    public String newTicket(HttpSession session, Model model) {
        try {
            model.addAttribute("categories", categoryCatalogService.ensureDefaultsAndList());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        if (SessionAuth.getUserId(session) == null) {
            model.addAttribute("error", "Ticket olusturmak icin once giris yapmalisiniz.");
        }
        return "tickets/new";
    }

    @PostMapping
    public String create(@RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                         HttpSession session,
                         Model model) {
        Long userId = SessionAuth.getUserId(session);
        if (userId == null) {
            model.addAttribute("error", "Ticket kaydetmek icin once giris yapmalisiniz.");
            try {
                model.addAttribute("categories", categoryCatalogService.ensureDefaultsAndList());
            } catch (Exception ignored) {
                // Keep the auth error visible if category bootstrap also fails.
            }
            return "tickets/new";
        }

        try {
            ticketService.createTicket(userId, title, description, categoryId);
            return "redirect:/tickets";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            try {
                model.addAttribute("categories", categoryCatalogService.ensureDefaultsAndList());
            } catch (Exception ignored) {
                // Keep the original create error visible if category bootstrap also fails.
            }
            return "tickets/new";
        }
    }
}
