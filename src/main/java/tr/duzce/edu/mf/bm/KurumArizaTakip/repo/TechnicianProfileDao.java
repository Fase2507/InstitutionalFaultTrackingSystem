package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.TechnicianProfile;

import java.util.List;
import java.util.Optional;

@Repository
public class TechnicianProfileDao extends AbstractHibernateDao {

    public TechnicianProfileDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<TechnicianProfile> findById(Long id) {
        return Optional.ofNullable(currentSession().get(TechnicianProfile.class, id));
    }

    public Optional<TechnicianProfile> findByUserId(Long userId) {
        return currentSession()
                .createQuery("select tp from TechnicianProfile tp where tp.user.id = :userId", TechnicianProfile.class)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }

    public List<TechnicianProfile> findActiveByCategoryId(Long categoryId) {
        return currentSession()
                .createQuery(
                        "select distinct tp from TechnicianProfile tp join tp.categories c " +
                                "where tp.active = true and c.id = :categoryId",
                        TechnicianProfile.class
                )
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public void save(TechnicianProfile profile) {
        currentSession().persist(profile);
    }

    public TechnicianProfile merge(TechnicianProfile profile) {
        return (TechnicianProfile) currentSession().merge(profile);
    }
}

