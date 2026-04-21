package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDao extends AbstractHibernateDao {

    public RoleDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(currentSession().get(Role.class, id));
    }

    public Optional<Role> findByRoleName(String roleName) {
        return currentSession()
                .createQuery("select r from Role r where r.roleName = :name", Role.class)
                .setParameter("name", roleName)
                .uniqueResultOptional();
    }

    public List<Role> findAll() {
        return currentSession().createQuery("select r from Role r order by r.roleName", Role.class).getResultList();
    }

    public void save(Role role) {
        currentSession().persist(role);
    }
}

