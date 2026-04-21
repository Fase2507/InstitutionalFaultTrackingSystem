package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class AbstractHibernateDao {

    protected final SessionFactory sessionFactory;

    protected AbstractHibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}

