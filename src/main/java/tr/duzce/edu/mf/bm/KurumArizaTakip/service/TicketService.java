package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Category;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Status;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Ticket;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.StatusDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.TicketDao;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.UserDao;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketDao ticketDao;
    private final UserDao userDao;
    private final StatusDao statusDao;
    private final AssignmentService assignmentService;
    private final CategorySuggestionService categorySuggestionService;

    public TicketService(TicketDao ticketDao,
                         UserDao userDao,
                         StatusDao statusDao,
                         AssignmentService assignmentService,
                         CategorySuggestionService categorySuggestionService) {
        this.ticketDao = ticketDao;
        this.userDao = userDao;
        this.statusDao = statusDao;
        this.assignmentService = assignmentService;
        this.categorySuggestionService = categorySuggestionService;
    }

    @Transactional
    public Ticket createTicket(Long reportedByUserId, String title, String description, Long categoryId) {
        User reporter = userDao.findById(reportedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("Kullanici bulunamadi."));
        if (!reporter.isEnabled()) {
            throw new IllegalStateException("Email dogrulanmadan ticket acilamaz.");
        }

        Category category = categorySuggestionService.resolveCategory(categoryId, title, description)
                .orElseThrow(() -> new IllegalStateException("Ticket icin kullanilabilir kategori bulunamadi."));

        Status open = statusDao.findByName("OPEN").orElseGet(() -> {
            Status s = Status.builder().statusName("OPEN").build();
            statusDao.save(s);
            return s;
        });

        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setReportedBy(reporter);
        ticket.setCategory(category);
        ticket.setStatus(open);

        assignmentService.pickTechnicianForCategory(category.getId())
                .ifPresent(tp -> ticket.setAssignedTo(tp.getUser()));

        ticketDao.save(ticket);
        return ticket;
    }

    @Transactional(readOnly = true)
    public List<Ticket> listTickets() {
        return ticketDao.findAll();
    }
}
