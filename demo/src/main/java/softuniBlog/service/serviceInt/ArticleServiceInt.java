package softuniBlog.service.serviceInt;

import softuniBlog.entity.Article;
import softuniBlog.entity.Category;
import softuniBlog.entity.Tag;
import softuniBlog.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleServiceInt {

    List<Article> findAllArticles();

    Article save(Article article);

    Optional<Article> findById(Integer id);

    void deleteArticle(Article article);

    boolean isUserAuthorOrAdmin(Article article);

    boolean articleExist(Integer id);

    Article editArticle(Article article, String title, String content, Category category, Set<Tag> tags);

    void deleteAllArticlesForCategory(Category category);

    Article createArticle(String title, String content, User author, Category category, Set<Tag> tags);

    void deleteAllArticlesForUser(User user);
}
