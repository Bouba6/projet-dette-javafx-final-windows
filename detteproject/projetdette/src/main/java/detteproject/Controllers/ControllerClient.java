package detteproject.Controllers;

import java.util.Scanner;

import detteproject.State.EtatDette;
import detteproject.core.UserConnected;
import detteproject.data.entities.Client;
import detteproject.data.entities.Dette;
import detteproject.data.entities.User;
import detteproject.services.ClientService;
import detteproject.services.DetteService;
import detteproject.views.DetteView;

public class ControllerClient {
    private final ClientService clientService;
    private final DetteView detteView;
    private final DetteService detteService;

    public ControllerClient(ClientService clientService, DetteView detteView, DetteService detteService) {
        this.clientService = clientService;
        this.detteView = detteView;
        this.detteService = detteService;
    }

    public void load() {
        System.out.println("Bonjour Client !");
        Scanner scanner = new Scanner(System.in);
        int choixClient;

        do {
            choixClient = menuClient(scanner);
            User user = UserConnected.getUserConnected();
            System.out.println(user);

            Client client = clientService.findClientByUser(user);
            switch (choixClient) {
                case 1:
                    detteView.findDette(client.getTelephone());
                    break;

                case 2:
                    Dette dette = detteView.saisie();
                    if (dette != null) {
                        dette.setEtat(EtatDette.ENCOURS);
                        detteService.save(dette);
                    } else {
                        System.out.println("Dette non enregistrée");
                    }
                    break;

                case 3:
                    System.out.println(client);
                    detteView.listDemandeDette(client);
                    break;

                case 4:
                    detteView.Relance(client);
                    break;

                case 0:
                    System.out.println("Au revoir!");
                    break;

                default:
                    System.out.println("Choix invalide!");
                    break;
            }
        } while (choixClient != 0);

        scanner.close();
    }

    public static int menuClient(Scanner scanner) {
        System.out.println("1--Lister dette non payée");
        System.out.println("2--Faire une demande de dette");
        System.out.println("3--Lister ces demandes de dette");
        System.out.println("4--Envoyer une relance pour une demande de dette annulée");
        System.out.println("0--QUITTER");

        System.out.print("Votre choix: ");
        if (scanner.hasNextInt()) {
            return scanner.nextInt();
        } else {
            scanner.next(); // clear invalid input
            return -1;
        }
    }
}
