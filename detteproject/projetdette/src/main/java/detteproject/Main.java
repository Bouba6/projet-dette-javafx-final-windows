package detteproject;

import detteproject.Auth.Connexion;
import detteproject.Controllers.ControllerAdmin;
import detteproject.Controllers.ControllerBoutiquier;
import detteproject.Controllers.ControllerClient;
import detteproject.core.UserConnected;
import detteproject.core.Config.Repositorie;
import detteproject.core.Factory.Impl.FactoryRepo;
import detteproject.core.Factory.Impl.FactoryService;
import detteproject.core.Factory.Impl.FactoryView;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.data.entities.User;
import detteproject.services.ArticleService;
import detteproject.services.DetteService;
import detteproject.services.PaiementService;
import detteproject.services.ClientService;
import detteproject.services.UserService;
import detteproject.views.ArticleView;
import detteproject.views.ClientView;
import detteproject.views.DetteView;
import detteproject.views.PaiementView;
import detteproject.views.UserView;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Use the factory classes to get repositories
        FactoryRepo<Client> clientRepoFactory = new FactoryRepo<>(Client.class);
        FactoryRepo<User> userRepoFactory = new FactoryRepo<>(User.class);
        FactoryRepo<Article> articleRepoFactory = new FactoryRepo<>(Article.class);
        FactoryRepo<Dette> detteRepoFactory = new FactoryRepo<>(Dette.class);
        FactoryRepo<Paiement> paiementRepoFactory = new FactoryRepo<>(Paiement.class);

        // Create repositories
        Repositorie<Client> clientRepository = clientRepoFactory.createRepository();
        Repositorie<User> userRepository = userRepoFactory.createRepository();
        Repositorie<Article> articleRepository = articleRepoFactory.createRepository();
        Repositorie<Dette> detteRepository = detteRepoFactory.createRepository();
        Repositorie<Paiement> paiementRepository = paiementRepoFactory.createRepository();

        // Use the factory classes to get services
        FactoryService<Client> clientServiceFactory = new FactoryService<>(Client.class, clientRepository);
        FactoryService<User> userServiceFactory = new FactoryService<>(User.class, userRepository);
        FactoryService<Article> articleServiceFactory = new FactoryService<>(Article.class, articleRepository);
        FactoryService<Dette> detteServiceFactory = new FactoryService<>(Dette.class, detteRepository);
        FactoryService<Paiement> paiementServiceFactory = new FactoryService<>(Paiement.class, paiementRepository);

        // Get services from the factory
        ClientService clientService = (ClientService) clientServiceFactory.createService();
        UserService userService = (UserService) userServiceFactory.createService();
        ArticleService articleService = (ArticleService) articleServiceFactory.createService();
        DetteService detteService = (DetteService) detteServiceFactory.createService();
        PaiementService paiementService = (PaiementService) paiementServiceFactory.createService();

        // Manually creating other service objects
        // ArticleService articleService = new ArticleService(new ArticleRepository());
        // DetteService detteService = new DetteService(new RepositorieJpaDette());

        // Use the FactoryView to create the UserView first
        FactoryView<User> userViewFactory = new FactoryView<>(User.class, clientService, articleService, userService,
                null, detteService);
        UserView userView = (UserView) userViewFactory.createView();

        // Now create the ClientView, passing the UserView
        FactoryView<Client> clientViewFactory = new FactoryView<>(Client.class, clientService, articleService,
                userService, userView, detteService);
        ClientView clientView = (ClientView) clientViewFactory.createView();

        FactoryView<Article> articleViewFactory = new FactoryView<>(Article.class, clientService, articleService,
                userService, userView, detteService);

        ArticleView articleView = (ArticleView) articleViewFactory.createView();

        FactoryView<Dette> detteViewFactory = new FactoryView<>(Dette.class, clientService, articleService,
                userService, userView, detteService);

        DetteView detteView = (DetteView) detteViewFactory.createView();

        FactoryView<Paiement> paiementViewFactory = new FactoryView<>(Paiement.class, clientService, articleService,
                userService, userView, detteService);

        PaiementView paiementView = (PaiementView) paiementViewFactory.createView();

        clientView.setScanner(scanner);
        articleView.setScanner(scanner);
        detteView.setScanner(scanner);
        userView.setScanner(scanner);
        paiementView.setScanner(scanner);
        detteView.setScanner(scanner);

        int choix = 0;

        // // creation des utilisateurs
        // User user1 = new User(Role.Admin, "Admin", "admin");
        // userService.save(user1);

        // user1 = new User(Role.Boutiquier, "Boutiquier", "boutiquier");
        // userService.save(user1);

        // user1 = new User(Role.Client, "Client", "client");
        // Client client2 = new Client();
        // client2.setNom("client");
        // client2.setAdresse("adresse");
        // client2.setTelephone("telephone");
        // client2.setSolde(0.0);
        // userService.save(user1);
        // client2.setUser(user1);
        // clientService.save(client2);

        do {

            scanner.nextLine();
            Connexion connection = new Connexion(userService);
            User user = connection.connexion();
            userView.afficher(userService.show());
            if (user != null) {
                UserConnected.setUserConnected(user);
                System.out.println("voici le menu qui vous ait offert  " + user.getRole() + " !");
                switch (user.getRole()) {

                    case Admin: {
                        ControllerAdmin controllerAdmin = new ControllerAdmin(articleService, clientView, clientService,
                                userService, userView, articleView);
                        controllerAdmin.load();
                        break;
                    }
                    case Client: {
                        ControllerClient controllerClient = new ControllerClient(clientService, detteView,
                                detteService);
                        controllerClient.load();
                        break;
                    }
                    case Boutiquier: {
                        ControllerBoutiquier controllerBoutiquier = new ControllerBoutiquier(clientService,
                                detteService, detteView, paiementView, clientView, paiementService);
                        controllerBoutiquier.load();
                        break;
                    }
                }
            }
        } while (choix != 20);

    }

}