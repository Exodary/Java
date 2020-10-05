package softuniBlog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuniBlog.bindingModel.CategoryBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.service.ArticleService;
import softuniBlog.service.CategoryService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final ArticleService articleService;

    @Autowired
    public CategoryController(CategoryService categoryService, ArticleService articleService) {
        this.categoryService = categoryService;
        this.articleService = articleService;
    }


    @GetMapping("/")
    public String list(Model model){
        model.addAttribute("view", "admin/category/list");

        List<Category> categories = this.categoryService.findAllCategories();

        this.categoryService.listCategories(categories);

        model.addAttribute("categories", categories);

        return "base-layout";
    }

    @GetMapping("/create")
    public String create(Model model){
        model.addAttribute("view", "admin/category/create");

        return "base-layout";
    }

    @PostMapping("/create")
    public String createProcess(CategoryBindingModel categoryBindingModel){
        if(StringUtils.isEmpty(categoryBindingModel.getName())){
            return "redirect:/admin/categories/create";
        }

        Category category = this.categoryService.createCategory(categoryBindingModel.getName());

        this.categoryService.saveAndFlushCategoryData(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Integer id){
        if(this.categoryService.findById(id).orElse(null) == null){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryService.findById(id).orElse(null);

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id,
                              CategoryBindingModel categoryBindingModel){

        if(this.categoryService.findById(id).orElse(null) == null){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryService.findById(id).orElse(null);

        category.setName(categoryBindingModel.getName());

        this.categoryService.saveAndFlushCategoryData(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Integer id){
        if(this.categoryService.findById(id).orElse(null) == null){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryService.findById(id).orElse(null);

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/delete");

        return "base-layout";
    }

    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id){
        if(this.categoryService.findById(id).orElse(null) == null){
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryService.findById(id).orElse(null);

        this.articleService.deleteAllArticlesForCategory(category);
        this.categoryService.deleteCategory(category);

        return "redirect:/admin/categories/";
    }
}
