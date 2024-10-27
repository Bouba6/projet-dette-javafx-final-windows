package detteproject.views;

import java.time.LocalDateTime;
import java.util.List;

import detteproject.Repository.List.ArticleRepository;
import detteproject.core.UserConnected;
import detteproject.core.ViewImpl;
import detteproject.core.Config.Repositorie;
import detteproject.core.Config.Service;
import detteproject.data.entities.Article;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;

public class ArticleView extends ViewImpl<Article> {
    ArticleService articleService;

    public ArticleView(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public Article saisie() {
        Article article = new Article();
        scanner.nextLine();
        System.out.println("Saisir le libelle");
        article.setLibelle(scanner.nextLine());
        System.out.println("Saisir le prix");
        article.setPrix(scanner.nextDouble());
        System.out.println("Saisir la quantite en Stock");
        article.setQteStock(scanner.nextDouble());
        User currentUser = UserConnected.getUserConnected();
        article.setUserCreate(currentUser);
        article.setCreateAt(LocalDateTime.now());
        article.onPrePersist();
        System.out.println(article.getCreateAt());
        return article;
    }

    public void filter() {
        List<Article> articles = articleService.show();
        articles.stream().filter(article -> article.getQteStock() != 0).forEach(System.out::println);
    }

    public Article changeQte() {
        System.out.println("entrer l'id de l'article");
        int id = scanner.nextInt();
        Article article = articleService.findById(id);
        if (article != null) {
            article.setQteStock(askQuantite());
            article.setUpdateAt(LocalDateTime.now());
            article.setUserUpdate(UserConnected.getUserConnected());
            return article;
        } else {
            return null;
        }

    }

    private int askQuantite() {
        int qte;
        do {
            System.out.println("entrer la nouvelle quantite");
            return qte = scanner.nextInt();
        } while (qte <= 0);
    }

}
