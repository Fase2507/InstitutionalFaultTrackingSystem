package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public class TicketDao extends AbstractHibernateDao {

    public TicketDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(currentSession().get(Ticket.class, id));
    }

    public List<Ticket> findAll() {
        return currentSession()
                .createQuery(
                        "select distinct t from Ticket t " +
                                "left join fetch t.category " +
                                "left join fetch t.status " +
                                "left join fetch t.assignedTo " +
                                "order by t.createdAt desc",
                        Ticket.class
                )
                .getResultList();
    }

    public void save(Ticket ticket) {
        currentSession().persist(ticket);
    }

    public Ticket merge(Ticket ticket) {
        return (Ticket) currentSession().merge(ticket);
    }
}
