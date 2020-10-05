package softuniBlog.service.serviceInt;

import softuniBlog.entity.Article;
import softuniBlog.entity.Tag;

import java.util.HashSet;

public interface TagServiceInt {

    Tag save(Tag tag);

    Tag findByName(String name);

    HashSet<Tag> findTagsFromString(String tagString);

    String listAllTagsForArticle(Article article);
}
