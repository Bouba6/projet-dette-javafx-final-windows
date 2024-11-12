package detteproject.Controllers;

import java.util.Scanner;

import detteproject.data.entities.Article;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import detteproject.services.UserService;
import detteproject.views.ArticleView;
import detteproject.views.ClientView;
import detteproject.views.UserView;

public class ControllerAdmin {
    private final ArticleService articleService;
    private final ClientService clientService;
    private final ClientView clientView;
    private final UserService userService;
    private final UserView userView;
    private final ArticleView articleView;

    public ControllerAdmin(
            ArticleService articleService,
            ClientView clientView,
            ClientService clientService,
            UserService userService,
            UserView userView,
            ArticleView articleView) {
        this.articleService = articleService;
        this.clientView = clientView;
        this.clientService = clientService;
        this.userService = userService;
        this.userView = userView;
        this.articleView = articleView;
    }

    public void load() {
        System.out.println("Bonjour Admin !");
        int choixAdmin;
        Scanner scanner = new Scanner(System.in);

        do {
            choixAdmin = menuAdmin(scanner);

            switch (choixAdmin) {
                case 1:
                    clientView.afficher(clientService.show());
                    break;
                case 2:
                    User user = userView.saisie();
                    if (user != null) {
                        userService.save(user);
                    } else {
                        System.out.println("User non enregistré");
                    }
                    break;
                case 3:
                    userView.afficher(userService.show());
                    userView.ChangeState();
                    break;
                case 4:
                    userView.afficher(userService.show());
                    break;
                case 5:
                    userView.afficher(userService.show());
                    userView.afficher(userView.findByRole());
                    break;
                case 6:
                    userView.afficher(userService.show());
                    userView.afficher(userView.findByEtat());
                    break;
                case 7:
                    Article article = articleView.saisie();
                    if (article != null) {
                        articleService.save(article);
                    } else {
                        System.out.println("Article non enregistré");
                    }
                    break;
                case 8:
                    articleView.afficher(articleService.show());
                    break;
                case 9:
                    articleView.filter();
                    break;
                case 10:
                    articleView.afficher(articleService.show());
                    Article articleToUpdate = articleView.changeQte();
                    if (articleToUpdate != null) {
                        articleService.update(articleToUpdate);
                    } else {
                        System.out.println("Article non modifié");
                    }
                    break;
                case 0:
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Choix invalide!");
                    break;
            }
        } while (choixAdmin != 0);

        scanner.close();
    }

    private int menuAdmin(Scanner scanner) {
        System.out.println("1---Créer un compte pour un client sans compte");
        System.out.println("2---Créer un compte user");
        System.out.println("3---Désactiver un compte User");
        System.out.println("4---Lister les users");
        System.out.println("5---Afficher les comptes par rôle");
        System.out.println("6---Afficher les comptes actifs");
        System.out.println("7---Saisie Article");
        System.out.println("8--Afficher Article");
        System.out.println("9--Filtrer Article par disponibilité");
        System.out.println("10--Mettre à jour la quantité en stock");
        System.out.println("0--Quitter");

        System.out.print("Votre choix: ");

        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            scanner.next(); // clear the invalid input
            return -1;
        }
    }
}
