package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Tag;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.serviceInt.ArticleServiceInt;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ArticleService implements ArticleServiceInt {

    private final ArticleRepository articleRepository;

    private final UserRepository userRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Article> findAllArticles() {
        return this.articleRepository.findAll();
    }

    @Override
    public Article save(Article article) {
        return this.articleRepository.saveAndFlush(article);
    }

    @Override
    public Optional<Article> findById(Integer id) {
        return this.articleRepository.findById(id);
    }

    @Override
    public void deleteArticle(Article article) {
        this.articleRepository.delete(article);
    }

    @Override
    public boolean isUserAuthorOrAdmin(Article article) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAdmin() || userEntity.isAuthor(article);
    }

    @Override
    public boolean articleExist(Integer id) {
        if(this.articleRepository.findById(id).orElse(null) == null){
            return false;
        }

        else{
            return true;
        }
    }

    @Override
    public Article editArticle(Article article, String title, String content, Category category, Set<Tag> tags) {
        article.setTitle(title);
        article.setContent(content);
        article.setCategory(category);
        article.setTags(tags);

        return article;
    }

    @Override
    public void deleteAllArticlesForCategory(Category category) {
        for(Article article : category.getArticles()){
            deleteArticle(article);
        }
    }

    @Override
    public Article createArticle(String title, String content, User author, Category category, Set<Tag> tags) {
        Article article = new Article();

        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);
        article.setCategory(category);
        article.setTags(tags);

        return article;
    }

    @Override
    public void deleteAllArticlesForUser(User user) {
        for(Article article : user.getArticles()){
            this.articleRepository.delete(article);
        }
    }

}
