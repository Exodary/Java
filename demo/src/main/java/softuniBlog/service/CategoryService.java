package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuniBlog.entity.Category;
import softuniBlog.repository.CategoryRepository;
import softuniBlog.service.serviceInt.CategoryServiceInt;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService implements CategoryServiceInt {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAllCategories() {
       return this.categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return this.categoryRepository.findById(id);
    }

    @Override
    public Category saveAndFlushCategoryData(Category category) {
        return this.categoryRepository.saveAndFlush(category);
    }

    @Override
    public void deleteCategory(Category category) {
        this.categoryRepository.delete(category);
    }

    @Override
    public void listCategories(List<Category> categories) {

         categories.stream()
                .sorted(Comparator.comparingInt(Category::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Category createCategory(String name) {
        Category category = new Category(name);

        return category;
    }
}
