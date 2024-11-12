package detteproject.views;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import detteproject.State.Etat;
import detteproject.State.EtatDette;
import detteproject.State.StateDette;
import detteproject.core.ViewImpl;
import detteproject.data.entities.Article;
import detteproject.data.entities.Client;
import detteproject.data.entities.DetailDette;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.services.ArticleService;
import detteproject.services.ClientService;
import detteproject.services.DetteService;

public class DetteView extends ViewImpl<Dette> {
    ArticleService articleService;
    ClientService clientService;
    DetteService detteService;

    public DetteView(ArticleService articleService, ClientService clientService, DetteService detteService) {
        this.articleService = articleService;
        this.clientService = clientService;
        this.detteService = detteService;
    }

    @Override
    public Dette saisie() {
        int attempts = 0;
        Client client = null;

        // Allow up to 3 attempts to find the client by phone number
        while (attempts < 3 && client == null) {
            scanner.nextLine(); // Clear the scanner buffer
            System.out.println("Entrer le numero de telephone du client:");
            String tel = scanner.nextLine();

            // Attempt to find the client by phone number
            client = clientService.find(tel);

            if (client == null) {
                attempts++;
                if (attempts < 3) {
                    System.out.println("Client non existant. Il vous reste " + (3 - attempts)
                            + " tentative(s). Veuillez réessayer.");
                } else {
                    System.out.println("Client non existant après 3 tentatives.");
                    return null; // Return null if the client is not found within 3 attempts
                }
            }
        }

        // If client is found, proceed with creating a Dette
        return askDette(client);
    }

    public void Relance(Client client) {
        scanner.nextLine();
        List<Dette> dettes = detteService.ListDetEc(client);
        if (dettes.isEmpty()) {
            System.out.println("Aucune dette");
            return;
        }
        System.out.println("ENTRER L'ID DE LA Dette");
        int id = scanner.nextInt();
        Dette dette = detteService.getById(id, client);

        if (dette == null) {
            System.out.println("Dette non existante");
            return;
        }
        if (dette.getEtat().equals(EtatDette.ANNULER)) {
            dette.setEtat(EtatDette.ENCOURS);
            detteService.update(dette);
        }
    }

    public Dette saisie1(Client client) {
        scanner.nextLine();
        if (client == null) {
            System.out.println("Client non existant");
            return null;
        } else {

            return askDette(client);

        }
    }

    public Dette askDette(Client client) {
        Dette dette = new Dette();
        do {
            System.out.println("Entrer l'id de l'article");
            int id = scanner.nextInt();
            Article article = articleService.findById(id);
            if (article == null) {
                System.out.println("Article non existant");
                continue;
            } else {
                int qte;
                int attempts = 0;
                final int maxAttempts = 3;
                do {
                    System.out.println(article);
                    System.out.println("Entrer la quantite");
                    qte = scanner.nextInt();
                } while (article.getQteStock() < qte && article.getQteStock() != 0 && ++attempts < maxAttempts);
                if (attempts >= maxAttempts) {
                    System.out.println("Trop d'essais. Opération annulée.");
                    return null;
                }
                if (article.getQteStock() == 0) {
                    System.out.println("Stock vide");
                    System.out.println("Veuillez saisir un autre article");
                    continue;
                }
                DetailDette detail = doesExist(dette, article);
                if (detail != null) {
                    // System.out.println("L'article est deja dans la dette");
                    detail.setQte(detail.getQte() + qte);
                    article.setQteStock(article.getQteStock() - qte);
                } else {
                    dette.setMontantVerser(0);
                    DetailDette detailDette = new DetailDette();
                    detailDette.setArticle(article);
                    detailDette.setQte(qte);
                    detailDette.setDette(dette);
                    article.setQteStock(article.getQteStock() - qte);
                    dette.setDetails(detailDette);
                    dette.setState(StateDette.DESARCHIVER);
                    // dette.setMontant(dette.getMontant() + article.getPrix() * qte);
                    // dette.setMontantRestant(dette.getMontant());
                    dette.setClient(client);
                    client.setDettes(dette);

                }

            }
        } while (resp() == 1);
        if (dette.getDetails().size() == 0) {
            return null;
        }
        dette.setMontantRestant(dette.getMontant());
        dette.onPrePersist();
        return dette;

    }

    private int resp() {
        int rep;
        do {
            System.out.println("Voulez vous ajouter une autre article 1-Oui 2-Non");
            rep = scanner.nextInt();
            return rep;

        } while (rep != 1 && rep != 2);
    }

    public DetailDette doesExist(Dette dette, Article article) {
        for (DetailDette detail : dette.getDetails()) {
            if (detail.getArticle().getId() == article.getId()) {
                return detail;
            }
        }
        return null;

        // return dette.getDetails().stream().anyMatch(detailDette ->
        // detailDette.getArticle().getId() == article.getId());

    }

    public void findDette(String tel) {
        Client client = clientService.find(tel);
        if (client == null) {
            System.out.println("Client non existant Khayyy");
            return;
        }

        List<Dette> list = afficherDettesValides(client);

        if (list.isEmpty()) {
            System.out.println("Aucune dette");
            return;
        } else {
            for (Dette dette : list) {
                System.out.println(dette);
            }
        }

        int rep;
        do {
            rep = menuOptions();
            switch (rep) {
                case 1:
                    afficherInfosDette(client);
                    break;
                case 2:
                    afficherDetailsDette(client);
                    break;
                case 3:
                    afficherToutesLesDettes(client);
                    break;
                case 4:
                    break; // Sortir du menu
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        } while (rep != 4);
    }

    public List<Dette> afficherDettesValides(Client client) {

        List<Dette> list = detteService.ListDetEc(client);
        List<Dette> dets = new ArrayList<>();

        System.out.println("Mes dettes :");
        for (Dette dette : list) {
            if (dette.getEtat().equals(EtatDette.VALIDER) && dette.getMontantRestant() > 0) {
                dets.add(dette);
            }
        }
        return dets;
    }

    private int menuOptions() {
        System.out.println("Voulez-vous ?");
        System.out.println("1-Voir les infos d'une dette");
        System.out.println("2-Voir les détails de la dette");
        System.out.println("3-Voir toutes mes dettes");
        System.out.println("4-Quitter");
        return scanner.nextInt();
    }

    private void afficherInfosDette(Client client) {
        System.out.println("Entrer l'id de la dette");
        int id = scanner.nextInt();
        Dette dett = detteService.getById(id, client);
        if (dett == null) {
            System.out.println("Dette non existante");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Informations de la dette :");
        System.out.println(" ID : " + dett.getId());
        System.out.println(" Montant : " + dett.getMontant());
        System.out.println(" Date : " + dett.getCreateAt());
        System.out.println(" Montant restant : " + dett.getMontantRestant());
        System.out.println("--------------------------------------------------");
    }

    private void afficherDetailsDette(Client client) {
        System.out.println("Entrer l'id de la dette");
        int id = scanner.nextInt();
        Dette dett = detteService.getById(id, client);
        if (dett == null) {
            System.out.println("Dette non existante");
            return;
        }

        int x;
        do {
            x = ask2see();
            switch (x) {
                case 1:
                    afficherArticlesDansDette(dett);
                    break;
                case 2:
                    afficherPaiementsDansDette(dett);
                    break;
                case 3:
                    break; // Quitter les détails
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        } while (x != 3);
    }

    private void afficherArticlesDansDette(Dette dett) {
        List<DetailDette> art = detteService.findArtInDet(dett);
        for (DetailDette article : art) {
            System.out.println(article);
        }
    }

    private void afficherPaiementsDansDette(Dette dett) {
        List<Paiement> pai = detteService.ListDetPai(dett);
        for (Paiement p : pai) {
            System.out.println(p);
        }
    }

    private void afficherToutesLesDettes(Client client) {
        List<Dette> dettes = detteService.ListDetEc(client);
        for (Dette dett : dettes) {
            System.out.println(dett);
        }
    }

    // public Dette listDetEC() {

    // scanner.nextLine();
    // System.out.println("Entrer le numero de telephone du client");
    // String tel = scanner.nextLine();
    // Client client = clientService.find(tel);
    // if (client == null) {
    // System.out.println("Client non existant");
    // return null;
    // } else {
    // System.out.println("Entrer l'id de la dette");
    // int id = scanner.nextInt();
    // for (Dette dette : client.getDettes()) {

    // }
    // }
    // }

    public int ask2see() {
        System.out.println("1---Afficher les articles de la dette");
        System.out.println("2---Afficher les paiements effectué sur cette dette");
        System.out.println("3---Quitter");
        int choix = scanner.nextInt();
        return choix;
    }

    public void showDetteWithFilter() {
        // Étape 1 : Afficher toutes les dettes
        List<Dette> dettes = detteService.show();
        displayDettes(dettes);

        // Étape 2 : Filtrer les dettes si l'utilisateur le souhaite
        if (askForFilter()) {
            dettes = filterDettesByState();
            displayDettes(dettes);
        }

        // Étape 3 : Demander si l'utilisateur souhaite changer l'état d'une dette
        if (askForStateChange()) {
            changeDetteState();
        }

    }

    public void listDemandeDette(Client client) {
        if (client == null) {
            System.out.println("Client non existant COMMENT CA");
            return;
        }
        List<Dette> dettes = detteService.ListDetEc(client);
        displayDettes(dettes);
        System.out.println("Filtrer vos dettes ? 1-Oui 0-Non");
        int rep = scanner.nextInt();
        if (rep == 1) {
            EtatDette etat = choiceFilter();
            for (Dette dette : dettes) {
                System.out.println(dette.getEtat());
                if (dette.getEtat().compareTo(etat) == 0) {
                    System.out.println(dette);
                }
            }
        }

    }

    public EtatDette choiceFilter() {
        System.out.println("Voulez vous les filtrer par 1-Encours 2-ANNULER 3-VALIDER");
        int rep = scanner.nextInt();
        EtatDette etat = null;
        do {
            switch (rep) {
                case 1:
                    etat = EtatDette.ENCOURS;
                    break;
                case 2:
                    etat = EtatDette.ANNULER;
                    break;
                case 3:
                    etat = EtatDette.VALIDER;
                    break;
                default:
                    break;
            }
        } while (rep != 1 && rep != 2 && rep != 3);
        return etat;
    }

    // Fonction pour afficher toutes les dettes
    private List<Dette> displayDettes(List<Dette> dettes) {

        for (Dette dette : dettes) {
            System.out.println(dette);
        }
        return dettes;
    }

    // Fonction pour demander à l'utilisateur s'il souhaite filtrer les dettes
    private boolean askForFilter() {
        System.out.println("Voulez vous les filtrer ?");
        System.out.println("1---Oui");
        System.out.println("2---Non");
        int rep = scanner.nextInt();
        return rep == 1;
    }

    // Fonction pour filtrer les dettes par état (EN_COURS ou ANNULER)
    private List<Dette> filterDettesByState() {
        EtatDette etat = choiceFilter();
        return detteService.ListDetByEtat(etat);
    }

    // Fonction pour demander à l'utilisateur s'il souhaite changer l'état d'une
    // dette
    private boolean askForStateChange() {
        System.out.println("Voulez vous changer l'etat d'une dette ?");
        System.out.println("1---Oui");
        System.out.println("2---Non");
        int rep = scanner.nextInt();
        return rep == 1;
    }

    // Fonction pour changer l'état d'une dette spécifique
    private void changeDetteState() {
        System.out.println("Voulez vous changer l'etat d'une dette a 1-VALIDER 2-REFUSER");
        int rep = scanner.nextInt();
        System.out.println("Entrer l'id de la dette");
        int id = scanner.nextInt();
        scanner.nextLine(); // consomme le saut de ligne
        System.out.println("Entrer le numero de telephone du client");
        String tel = scanner.nextLine();

        // Vérifier l'existence du client
        Client client = clientService.find(tel);
        if (client == null) {
            System.out.println("Client non existant");
            return;
        }

        // Vérifier l'existence de la dette
        Dette dette = detteService.getById(id, client);
        if (dette == null || dette.getEtat().equals(EtatDette.VALIDER)) {
            System.out.println("Dette non existante");
            return;
        }

        // Changer l'état de la dette en fonction de la sélection de l'utilisateur
        EtatDette etat = (rep == 1) ? EtatDette.VALIDER : EtatDette.ANNULER;
        dette.setEtat(etat);
        if (dette.getEtat().equals(EtatDette.VALIDER)) {
            System.out.println("hii");
            List<DetailDette> details = detteService.findArtInDet(dette);
            for (DetailDette articles : details) {
                System.out.println("hii");
                articleService.update(articles.getArticle());
            }
        }

        detteService.update(dette);
    }

}