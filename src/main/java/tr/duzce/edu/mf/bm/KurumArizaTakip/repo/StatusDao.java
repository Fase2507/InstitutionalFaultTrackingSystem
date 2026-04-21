package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Status;

import java.util.List;
import java.util.Optional;

@Repository
public class StatusDao extends AbstractHibernateDao {

    public StatusDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Status> findById(Long id) {
        return Optional.ofNullable(currentSession().get(Status.class, id));
    }

    public Optional<Status> findByName(String statusName) {
        return currentSession()
                .createQuery("select s from Status s where s.statusName = :name", Status.class)
                .setParameter("name", statusName)
                .uniqueResultOptional();
    }

    public List<Status> findAll() {
        return currentSession().createQuery("select s from Status s order by s.statusName", Status.class).getResultList();
    }

    public void save(Status status) {
        currentSession().persist(status);
    }
}

