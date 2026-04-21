package tr.duzce.edu.mf.bm.KurumArizaTakip.repo;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDao extends AbstractHibernateDao {

    public CategoryDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(currentSession().get(Category.class, id));
    }

    public Optional<Category> findByName(String catName) {
        return currentSession()
                .createQuery("select c from Category c where c.catName = :name", Category.class)
                .setParameter("name", catName)
                .uniqueResultOptional();
    }

    public List<Category> findAll() {
        return currentSession().createQuery("select c from Category c order by c.catName", Category.class).getResultList();
    }

    public void save(Category category) {
        currentSession().persist(category);
    }
}

