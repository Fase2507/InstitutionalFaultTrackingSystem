package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.VerificationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VerificationTokenDao extends AbstractHibernateDao {

    public VerificationTokenDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<VerificationToken> findById(Long id) {
        return Optional.ofNullable(currentSession().get(VerificationToken.class, id));
    }

    public List<VerificationToken> findActiveByUserId(Long userId, LocalDateTime now) {
        return currentSession()
                .createQuery(
                        "select vt from VerificationToken vt " +
                                "where vt.user.id = :userId and vt.usedAt is null and vt.expiresAt > :now " +
                                "order by vt.sentAt desc",
                        VerificationToken.class
                )
                .setParameter("userId", userId)
                .setParameter("now", now)
                .getResultList();
    }

    public void save(VerificationToken token) {
        currentSession().persist(token);
    }

    public VerificationToken merge(VerificationToken token) {
        return (VerificationToken) currentSession().merge(token);
    }
}

