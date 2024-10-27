package detteproject;

import detteproject.State.EtatDette;
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

        // // Initialize other views
        // ArticleView articleView = new ArticleView(articleService);
        // DetteView detteView = new DetteView(articleService, clientService);

        // Set scanners for the views
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
            System.out.println("Entrer votre login");
            String login = scanner.nextLine();
            System.out.println("Entrer votre mot de passe");
            String password = scanner.nextLine();
            User user = userService.findByLogin(login, password);
            userView.afficher(userService.show());
            System.out.println(user);
            if (user != null) {
                UserConnected.setUserConnected(user);
                System.out.println("Bonjour " + user.getRole() + " !");
                System.out.println("Bonjour " + user.getClient() + " !");
                switch (user.getRole()) {

                    case Admin: {
                        int choixAdmin;
                        do {
                            menuAdmin();
                            choixAdmin = scanner.nextInt();
                            switch (choixAdmin) {
                                case 1: {
                                    clientView.afficher(clientService.show());
                                    clientService.update(clientView.updateClient());
                                    break;
                                }
                                case 2: {
                                    userService.save(userView.saisie());
                                    break;
                                }
                                case 3: {
                                    userView.afficher(userService.show());
                                    userView.ChangeState();
                                    break;
                                }
                                case 4: {
                                    userView.afficher(userService.show());
                                    break;
                                }
                                case 5: {
                                    userView.afficher(userService.show());
                                    userView.afficher(userView.findByRole());
                                    break;
                                }
                                case 6: {
                                    userView.afficher(userService.show());
                                    userView.afficher(userView.findByEtat());
                                    break;
                                }
                                case 7: {
                                    articleService.save(articleView.saisie());
                                    break;
                                }
                                case 8: {
                                    articleView.afficher(articleService.show());
                                    break;
                                }
                                case 9: {

                                    articleView.filter();
                                    break;
                                }
                                case 10: {
                                    articleView.afficher(articleService.show());
                                    articleService.update(articleView.changeQte());
                                    break;
                                }

                            }
                        } while (choixAdmin != 0);
                        break;
                    }
                    case Client: {
                        int choixClient;
                        do {
                            menuClient();
                            choixClient = scanner.nextInt();
                            System.out.println(user);

                            Client client = clientService.findClientByUser(user);

                            System.out.println(client);

                            switch (choixClient) {
                                case 1: {

                                    detteView.findDette(client.getTelephone());
                                    break;
                                }
                                case 2: {
                                    Dette dette = detteView.saisie1(client);
                                    dette.setEtat(EtatDette.ENCOURS);
                                    detteService.save1(dette);
                                    break;
                                }
                                case 3: {
                                    System.out.println(client);
                                    detteView.listDemandeDette(client);
                                    break;
                                }
                                case 4: {
                                    detteView.Relance(client);
                                    break;
                                }
                            }
                        } while (choixClient != 0);
                        break;
                    }
                    case Boutiquier: {
                        int choixBoutiquier;
                        do {
                            menuBoutiquier();
                            choixBoutiquier = scanner.nextInt();
                            switch (choixBoutiquier) {
                                case 1: {
                                    clientService.save(clientView.saisie());
                                    break;
                                }
                                case 2: {
                                    clientView.afficher(clientService.show());
                                    break;
                                }
                                case 3: {
                                    clientView.afficher(clientView.show());
                                    clientView.finding();
                                    break;
                                }
                                case 4: {
                                    Dette dette = detteView.saisie();
                                    dette.setEtat(EtatDette.VALIDER);
                                    detteService.save(dette);
                                    break;
                                }
                                case 5: {
                                    detteView.afficher(detteService.show());
                                    break;
                                }
                                case 6: {
                                    paiementService.save(paiementView.saisie());
                                    break;
                                }
                                case 7: {
                                    scanner.nextLine();
                                    System.out.println("Entrer le numero de telephone du client");
                                    String tel = scanner.nextLine();
                                    detteView.findDette(tel);
                                    break;

                                }

                                case 8: {
                                    detteView.showDetteWithFilter();
                                    break;
                                }

                            }
                        } while (choixBoutiquier != 0);
                        break;
                    }
                }
            }
        } while (choix != 20);

    }

    public static int menu() {
        System.out.println("1---Créer un client");
        System.out.println("2---Lister Clients");
        System.out.println("3---Creer un compte pour un client sans compte");
        System.out.println("4---Creer un compte user");
        System.out.println("5---Desactiver un compte User");
        System.out.println("6---Lister les users");
        System.out.println("7---Afficher les comptes par role");
        System.out.println("8---Afficher les comptes actifs");
        System.out.println("9---Saisie Article");
        System.out.println("10--Afficher Article");
        System.out.println("11--Filtrer Article par disponibilité");
        System.out.println("12--Mettre à jour la quantité en stock");
        System.out.println("13--Saisie dette");
        System.out.println("14--Lister dette");
        System.out.println("15--Afficher compte par son téléphone");
        System.out.println("16--Afficher les clients ayant un compte  user");
        System.out.println("17--Lister les informations d'un client par son numéro");
        System.out.println("18--Faire le paiement d'une dette");
        System.out.println("19--Lister dette non payée");

        System.out.println("--QUITTER");
        return scanner.nextInt();
    }

    public static int menuClient() {
        System.out.println("1--Lister dette non payée");
        System.out.println("2--Faire une demande de dette");
        System.out.println("3--Lister ces demandes de dette");
        System.out.println("4--Envoyer une relance pour une  demande de dette annulere");
        System.out.println("--QUITTER");
        return scanner.nextInt();
    }

    public static int menuAdmin() {
        System.out.println("1---Creer un compte pour un client sans compte");
        System.out.println("2---Creer un compte user");
        System.out.println("3---Desactiver un compte User");
        System.out.println("4---Lister les users");
        System.out.println("5---Afficher les comptes par role");
        System.out.println("6---Afficher les comptes actifs");
        System.out.println("7---Saisie Article");
        System.out.println("8--Afficher Article");
        System.out.println("9--Filtrer Article par disponibilité");
        System.out.println("10--Mettre à jour la quantité en stock");
        return scanner.nextInt();
    }

    public static int menuBoutiquier() {
        System.out.println("1---Créer un client");
        System.out.println("2---Lister Clients");
        System.out.println("3--Lister les informations d'un client par son numéro");
        System.out.println("4--Saisie dette");
        System.out.println("5--Lister dette");
        System.out.println("6--Faire le paiement d'une dette");
        System.out.println("7--Lister dette non payée");
        System.out.println("8--Lister les demandes de dettes en cours");
        System.out.println("--QUITTER");
        return scanner.nextInt();
    }
}

// choix = menu();
// switch (choix) {
// case 1: {
// clientService.save(clientView.saisie());
// break;
// }
// case 2: {
// clientView.afficher(clientService.show());
// break;
// }
// case 3: {
// clientView.afficher(clientService.show());
// clientService.update(clientView.updateClient());
// break;
// }
// case 4: {
// userService.save(userView.saisie());
// break;
// }
// case 5: {
// userView.afficher(userService.show());
// userView.ChangeState();
// break;
// }
// case 6: {
// userView.afficher(userService.show());
// break;
// }
// case 7: {
// userView.afficher(userService.show());
// userView.afficher(userView.findByRole());
// break;
// }
// case 8: {
// userView.afficher(userService.show());
// userView.afficher(userView.findByEtat());
// break;
// }
// case 9: {
// articleService.save(articleView.saisie());
// break;
// }
// case 10: {
// articleView.afficher(articleService.show());
// break;
// }
// case 11: {

// articleView.filter();
// break;
// }
// case 12: {
// articleView.afficher(articleService.show());
// articleService.update(articleView.changeQte());
// break;
// }
// case 13: {
// detteService.save(detteView.saisie());
// break;
// }
// case 14: {
// detteView.afficher(detteService.show());
// break;
// }
// case 15: {
// clientView.afficher(clientView.show());
// clientView.finding();
// break;
// }
// case 16: {
// clientView.afficher(clientView.show());
// break;
// }
// case 17: {
// clientView.finding();
// break;
// }
// case 18: {
// paiementService.save(paiementView.saisie());
// break;
// }
// case 19: {
// Dette dette = detteView.findDette();
// System.out.println("--------------------------------------------------");
// System.out.println("Informations de la dette :");
// System.out.println(" ID : " + dette.getId());
// System.out.println(" Montant : " + dette.getMontant());
// System.out.println(" Date : " + dette.getDate());
// System.out.println(" Montant restant : " + dette.getMontantRestant());
// System.out.println("--------------------------------------------------");
// int x = detteView.ask2see();
// switch (x) {
// case 1: {
// articleView.afficher(detteService.findArtInDet(dette));
// break;
// }

// case 2: {

// paiementView.afficher(detteService.ListDetPai(dette));
// break;

// }
// case 3:
// break;
// }
// }
// default:
// break;
// }
