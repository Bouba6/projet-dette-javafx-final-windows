package detteproject.core;

import detteproject.core.Config.Service;
import detteproject.data.entities.Article;

public interface ServiceArticle extends Service<Article> {
    Article findById(int id);
}
