package detteproject.Repository.Bd;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.time.ZoneId;

import detteproject.core.RepositorieArticle;
import detteproject.core.RepositoryBdImpl;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;

public class ArticleRepositoryBD extends RepositoryBdImpl<Article> implements RepositorieArticle {

    public ArticleRepositoryBD() {
        super(Article.class);
    }

    @Override
    public void update(Article objet) {
        System.out.println("hiii");
        super.update(objet);
    }

    @Override
    public List<Article> selectAll() {
        try {
            return super.selectAll(null);
        } catch (InstantiationException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Article getById(int id) {
        String condition = "id = '" + id + "'"; // Use "id = " + id
        List<Article> article;
        try {
            article = super.selectAll(condition);
            return article.isEmpty() ? null : article.get(0);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String getTableName() {
        return "article";
    }

    @Override
    protected List<String> getColumnNames() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnNames'");
    }

    @Override
    protected List<String> excludedFieldsInsert() {
        return List.of("id", "updateAt", "userUpdate");
    }

    @Override
    public boolean insert(Article objet) {
        super.insert(objet);
        return true;
    }

    @Override
    protected List<String> excludedFieldsUpdate() {
        return List.of("id", "createAt", "userCreate");
    }

    @Override
    protected String[] column() {
        return new String[] { "id" };
    }

    @Override
    protected List<String> excludedFieldsSelect() {
        return List.of();
    }

}
