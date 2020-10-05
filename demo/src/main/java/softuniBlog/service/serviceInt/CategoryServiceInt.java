package softuniBlog.service.serviceInt;

import softuniBlog.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryServiceInt {
    List<Category> findAllCategories();

    Optional<Category> findById(Integer id);

    Category saveAndFlushCategoryData(Category category);

    void deleteCategory(Category category);

    void listCategories(List<Category> categories);

    Category createCategory(String name);

}
