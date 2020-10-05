package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.service.CategoryService;

import java.util.List;
import java.util.Set;

@Controller
public class HomeController {


    private final CategoryService categoryService;

    @Autowired
    public HomeController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index(Model model){

        List<Category> categories = this.categoryService.findAllCategories();

        model.addAttribute("view", "home/index");
        model.addAttribute("categories", categories);

        return "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model){
        model.addAttribute("view", "error/403");

        return "base-layout";
    }

    @GetMapping("/category/{id}")
    public String listArticles(Model model, @PathVariable Integer id){
        if(this.categoryService.findById(id).orElse(null) == null){
            return "redirect:/";
        }

        Category category = this.categoryService.findById(id).orElse(null);
        Set<Article> articles = category.getArticles();


        model.addAttribute("view", "home/list-articles");
        model.addAttribute("articles", articles);
        model.addAttribute("category", category);

        return "base-layout";
    }
}
