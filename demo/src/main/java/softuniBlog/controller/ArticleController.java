package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Tag;
import softuniBlog.entity.User;
import softuniBlog.service.ArticleService;
import softuniBlog.service.CategoryService;
import softuniBlog.service.TagService;
import softuniBlog.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ArticleController {


    private final ArticleService articleService;


    private final UserService userService;


    private final CategoryService categoryService;


    private final TagService tagService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService, CategoryService categoryService, TagService tagService) {
        this.articleService = articleService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }


    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){

        List<Category> categories = this.categoryService.findAllCategories();

        model.addAttribute("categories", categories);

        model.addAttribute("view", "article/create");

                return "base-layout";
    }

    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userService.findByEmail(user.getUsername());
        Category category = this.categoryService
                .findById(articleBindingModel.getCategoryId()).orElse(null);

        HashSet<Tag> tags = this.tagService.findTagsFromString(articleBindingModel.getTagString());

        Article articleEntity = this.articleService.createArticle(articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                userEntity,
                category,
                tags);

        this.articleService.save(articleEntity);

        return "redirect:/";
    }

    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id){

       if(this.articleService.findById(id).orElse(null) == null) {
           return "redirect:/";
       }

       if(!(SecurityContextHolder.getContext().getAuthentication()
       instanceof AnonymousAuthenticationToken)){

           UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                   .getAuthentication().getPrincipal();

           User entityUser = this.userService.findByEmail(principal.getUsername());

           model.addAttribute("user", entityUser);
       }

        Article article = this.articleService.findById(id).orElse(null);

        model.addAttribute("article", article);
        model.addAttribute("view", "article/details");

        return "base-layout";
    }

    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model){

        if(!this.articleService.articleExist(id)){
            return "redirect:/";
        }

        Article article = this.articleService.findById(id).orElse(null);

        if(!articleService.isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        List<Category> categories = this.categoryService.findAllCategories();

        String tagString = this.tagService.listAllTagsForArticle(article);

        model.addAttribute("view", "article/edit");
        model.addAttribute("article", article);
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tagString);

        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel articleBindingModel){

        if(!this.articleService.articleExist(id)){
            return "redirect:/";
        }

        Article article = this.articleService.findById(id).orElse(null);

        if(!articleService.isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        Category category = this.categoryService
                .findById(articleBindingModel.getCategoryId()).orElse(null);

        HashSet<Tag> tags = this.tagService.findTagsFromString(articleBindingModel.getTagString());

        this.articleService.editArticle(article, articleBindingModel.getTitle(),
                articleBindingModel.getContent(), category, tags);

        this.articleService.save(article);

        return "redirect:/article/" + article.getId();
    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id){

        if(!this.articleService.articleExist(id)){
            return "redirect:/";
        }

        Article article = this.articleService.findById(id).orElse(null);

        if(!articleService.isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {
        if (!this.articleService.articleExist(id)) {
            return "redirect:/";
        }

        Article article = this.articleService.findById(id).orElse(null);

        if(!this.articleService.isUserAuthorOrAdmin(article)) {
            return "redirect:/article/" + id;
        }

        this.articleService.deleteArticle(article);

        return "redirect:/";
    }
}
