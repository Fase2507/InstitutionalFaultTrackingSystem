package tr.duzce.edu.mf.bm.KurumArizaTakip.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.duzce.edu.mf.bm.KurumArizaTakip.entity.Category;
import tr.duzce.edu.mf.bm.KurumArizaTakip.repo.CategoryDao;

import java.util.List;

@Service
public class CategoryCatalogService {

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Yazilim",
            "Donanim",
            "Ag",
            "Tesisat",
            "Guvenlik",
            "Malzeme eksikligi",
            "Elektrik"
    );

    private final CategoryDao categoryDao;

    public CategoryCatalogService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Category> ensureDefaultsAndList() {
        List<Category> existing = categoryDao.findAll();
        if (!existing.isEmpty()) {
            return existing;
        }

        for (String categoryName : DEFAULT_CATEGORIES) {
            Category category = Category.builder()
                    .catName(categoryName)
                    .build();
            categoryDao.save(category);
        }

        return categoryDao.findAll();
    }
}
