package detteproject.core;

import detteproject.core.Config.Repositorie;
import detteproject.data.entities.Article;

public interface RepositorieArticle extends Repositorie<Article> {
    Article getById(int id);
}
