package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.User;

import java.util.Optional;

@Repository
public class UserDao extends AbstractHibernateDao {

    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(currentSession().get(User.class, id));
    }

    public Optional<User> findByEmail(String email) {
        return currentSession()
                .createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .uniqueResultOptional();
    }

    public void save(User user) {
        currentSession().persist(user);
    }

    public User merge(User user) {
        return (User) currentSession().merge(user);
    }
}

