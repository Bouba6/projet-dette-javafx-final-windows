package detteproject.Repository.List;

import java.util.ArrayList;
import java.util.List;

import detteproject.core.RepositorieArticle;
import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Article;

public class ArticleRepository implements RepositorieArticle {
    List<Article> articles = new ArrayList<>();
    private int lastId = 0;

    @Override
    public boolean insert(Article objet) {
        objet.setId(++lastId);
        articles.add(objet);
        return true;
    }

    @Override
    public void update(Article objet) {
        if (objet != null) {
            System.out.println("Modification reussie");
        } else {
            System.out.println("Modification echouée");
        }
    }

    @Override
    public List<Article> selectAll() {
        return articles;
    }

    @Override
    public Article getById(int id) {
        return articles.stream().filter(article -> article.getId() == id).findFirst().orElse(null);
    }

}
