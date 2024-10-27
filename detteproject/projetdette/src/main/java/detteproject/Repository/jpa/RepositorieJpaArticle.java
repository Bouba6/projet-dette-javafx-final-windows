package detteproject.Repository.jpa;

import detteproject.core.RepositorieArticle;
import detteproject.core.RepositoryJpaImpl;
import detteproject.data.entities.Article;

public class RepositorieJpaArticle extends RepositoryJpaImpl<Article> implements RepositorieArticle {

    public RepositorieJpaArticle() {
        super(Article.class);
    }

    @Override
    public Article getById(int id) {
        Article article = em.find(Article.class, id);
        if (article == null) {
            System.out.println("Article non existant");
            return null;
        } else {
            return article;
        }
    }
}
