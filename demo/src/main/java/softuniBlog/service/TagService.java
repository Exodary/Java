package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuniBlog.entity.Article;
import softuniBlog.entity.Tag;
import softuniBlog.repository.TagRepository;
import softuniBlog.service.serviceInt.TagServiceInt;

import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class TagService implements TagServiceInt {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag save(Tag tag) {
        return this.tagRepository.saveAndFlush(tag);
    }

    @Override
    public Tag findByName(String name) {
        return this.tagRepository.findByName(name);
    }

    @Override
    public HashSet<Tag> findTagsFromString(String tagString) {
        HashSet<Tag> tags = new HashSet<>();

        String[] tagNames = tagString.split(",\\s*");

        for(String tagName : tagNames){
            Tag currentTag = this.tagRepository.findByName(tagName);

            if(currentTag == null){
                currentTag = new Tag(tagName);
                this.tagRepository.save(currentTag);
            }

            tags.add(currentTag);
        }

        return tags;
    }

    @Override
    public String listAllTagsForArticle(Article article) {
        String tagString = article.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.joining(", "));

        return tagString;
    }


}
