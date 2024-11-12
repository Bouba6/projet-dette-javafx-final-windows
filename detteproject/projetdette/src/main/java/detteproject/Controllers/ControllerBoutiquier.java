package detteproject.Controllers;

import java.util.Scanner;

import detteproject.State.EtatDette;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.Paiement;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.services.PaiementService;
import detteproject.views.ClientView;
import detteproject.views.DetteView;
import detteproject.views.PaiementView;

public class ControllerBoutiquier {
    private final ClientService clientService;
    private final DetteService detteService;
    private final DetteView detteView;
    private final PaiementView paiementView;
    private final ClientView clientView;
    private final PaiementService paiementService;

    public ControllerBoutiquier(
            ClientService clientService,
            DetteService detteService,
            DetteView detteView,
            PaiementView paiementView,
            ClientView clientView,
            PaiementService paiementService) {
        this.clientService = clientService;
        this.detteService = detteService;
        this.detteView = detteView;
        this.paiementView = paiementView;
        this.clientView = clientView;
        this.paiementService = paiementService;
    }

    public void load() {
        System.out.println("Bonjour Boutiquier !");
        int choixBoutiquier;
        Scanner scanner = new Scanner(System.in);

        do {
            choixBoutiquier = menuBoutiquier(scanner);

            switch (choixBoutiquier) {
                case 1:
                    Client client = clientView.saisie();
                    if (client != null) {
                        clientService.save(client);
                    } else {
                        System.out.println("Client non enregistré");
                    }
                    break;

                case 2:
                    clientView.afficher(clientService.show());
                    break;

                case 3:
                    clientView.afficher(clientService.show());
                    clientView.finding();
                    break;

                case 4:
                    Dette dette = detteView.saisie();
                    if (dette != null) {
                        dette.setEtat(EtatDette.VALIDER);
                        detteService.save(dette);
                        System.out.println(dette);
                    } else {
                        System.out.println("Dette non enregistrée");
                    }
                    break;

                case 5:
                    detteView.afficher(detteService.show());
                    break;

                case 6:
                    Paiement paiement = paiementView.saisie();
                    if (paiement != null) {
                        paiementService.save(paiement);
                    } else {
                        System.out.println("Paiement non enregistré");
                    }
                    break;

                case 7:
                    System.out.print("Entrer le numéro de téléphone du client: ");
                    String tel = scanner.nextLine();
                    detteView.findDette(tel);
                    break;

                case 8:
                    detteView.showDetteWithFilter();
                    break;

                case 0:
                    System.out.println("Au revoir!");
                    break;

                default:
                    System.out.println("Choix invalide!");
                    break;
            }
        } while (choixBoutiquier != 0);

        scanner.close();
    }

    public static int menuBoutiquier(Scanner scanner) {
        System.out.println("1---Créer un client");
        System.out.println("2---Lister Clients");
        System.out.println("3---Lister les informations d'un client par son numéro");
        System.out.println("4---Saisie dette");
        System.out.println("5---Lister dette");
        System.out.println("6---Faire le paiement d'une dette");
        System.out.println("7---Lister dette non payée");
        System.out.println("8---Lister les demandes de dettes en cours");
        System.out.println("0---QUITTER");

        System.out.print("Votre choix: ");
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            scanner.next(); // clear invalid input
            return -1;
        }
    }
}
